# Start by building the application.
FROM golang:1.12 as build

WORKDIR /go/src/app
COPY . .

RUN go get -d -v ./...
RUN go install -v ./...

# Now copy it into our base image.
FROM gcr.io/distroless/base:debug
COPY --from=build /go/bin/app /
COPY config.yaml /
CMD ["/app -config-file config.yaml"]