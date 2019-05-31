#!/bin/bash


minikube start --memory=8192 --cpus=6  --vm-driver=hyperkit  --extra-config=apiserver.enable-admission-plugins="LimitRanger,NamespaceExists,NamespaceLifecycle,ResourceQuota,ServiceAccount,DefaultStorageClass,MutatingAdmissionWebhook"