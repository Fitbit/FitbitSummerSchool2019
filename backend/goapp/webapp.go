package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"

	"github.com/golang-migrate/migrate/v4"
	"github.com/jackc/pgx"
	"gopkg.in/yaml.v2"

	_ "github.com/golang-migrate/migrate/v4/database/postgres"
	_ "github.com/golang-migrate/migrate/v4/source/file"
)

var (
	// server settings
	configFilePath = flag.String("config-file", "", "Path to configuration file")
	config         Config
	cp             *pgx.ConnPool
)

type Config struct {
	Web struct {
		Addr string `yaml:"string"`
		Port int    `yaml:"port"`
	} `yaml:"web""`
	Database struct {
		Host     string `yaml:"host"`
		Port     uint16 `yaml:"port"`
		Schema   string `yaml:"schema"`
		Username string `yaml:"username"`
		Password string `yaml:"password"`
	} `yaml:"database""`
}

func main() {
	flag.Parse()

	rawConfig, err := ioutil.ReadFile(*configFilePath)
	if err != nil {
		log.Printf("Error reading configuration file at path %s: %+v", *configFilePath, err)
	}
	err = yaml.Unmarshal(rawConfig, &config)
	if err != nil {
		log.Printf("Error reading configuration file: %+v", err)
	}

	m, err := migrate.New(
		"file://db",
		fmt.Sprintf("postgres://%s:%s@%s:%d/%s?sslmode=disable",
			config.Database.Username,
			config.Database.Password,
			config.Database.Host,
			config.Database.Port,
			config.Database.Schema),
		)
	if err != nil {
		log.Panicf("Could not initialize migration: %+v", err)
	}
	err = m.Steps(1)
	if err != nil {
		log.Printf("Could not do db migrations: %+v", err)
	}

	cp, err = pgx.NewConnPool(pgx.ConnPoolConfig{
		ConnConfig: pgx.ConnConfig{
			Host:     config.Database.Host,
			Port:     config.Database.Port,
			User:     config.Database.Username,
			Password: config.Database.Password,
			Database: config.Database.Schema,
		},
		MaxConnections: 20,
	})
	if err != nil {
		log.Printf("Error opening database connection: %+v", err)
	}

	http.HandleFunc("/event", eventHandler)
	log.Fatal(http.ListenAndServe(fmt.Sprintf("%s:%d", config.Web.Addr, config.Web.Port), nil))
}

type Event struct {
	Type     string            `json:"type"`
	Metadata map[string]string `json:"metadata"`
}

func eventHandler(w http.ResponseWriter, r *http.Request) {
	// Read body
	b, err := ioutil.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}

	var event Event
	err = json.Unmarshal(b, &event)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}

	out, _ := json.Marshal(event)
	log.Printf("Saving event: %s", string(out))
	tag, err := cp.Exec("insert into events(data) values($1)", out)
	if err != nil {
		log.Printf("Error saving event to db: %+v", err)
		http.Error(w, err.Error(), 500)
		return
	}
	log.Printf("Saved event to db: %+v", tag)
	w.WriteHeader(http.StatusOK);
}
