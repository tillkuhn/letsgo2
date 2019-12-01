## launch with make -s to su
APPID=letsgo2
.DEFAULT_GOAL := help
.ONESHELL:
.SHELL := /usr/bin/bash
#A phony target is one that is not really the name of a file; rather it is just a name for a recipe to be executed when you make an explicit request. There are two reasons to use a phony target: to avoid a conflict with a file of the same name, and to improve performance.
.PHONY: help init plan apply dns ec2-stop ec2-start ec2-status login jardev jarprod \
        webdev webprod jardev deployjar deployui upload json-server mock  mockd mockstop clean
.SILENT: help ## no @s needed
.EXPORT_ALL_VARIABLES:
AWS_PROFILE = timafe
AWS_CMD ?= aws
APPID=letsgo2

# https://github.com/localstack/localstack/blob/master/Makefile get inspired
help:
	echo "Usage: make [target]"
	echo
	echo "Terraform Targets:"
	echo "  init        Inits infrastructure in infra with terraform"
	echo "  plan        Plans infrastructure in infra with terraform"
	echo "  apply       Applies infrastructure in infra with terraform (auto-approve)"
	echo "  dns         Syncs the DNS entry with the instances current public IP (auto-approve)"
	echo
	echo "EC2 Targets:"
	echo "  ec2-stop    Stops the EC2 instance"
	echo "  ec2-start   Starts the EC2 instance"
	echo "  ec2-status  Current Status of ec2 instance"
	echo "  login       ssh login to instance"
	echo
	echo "Gradle / Docker Build Targets:"
#	echo "  docker      Run docker build"
	echo "  jardev      Create bootJar optimized for production"
	echo "  jarprod     Create bootJar optimized for production"
	echo "  jarrun      Runs jar file with java "
	echo "  jardev      Create bootJar optimized for dev "
	echo "  webdev      Runs webpack build for frontend dev"
	echo "  webprod     Runs webpack build for frontend prod"
	echo "  deployjar   Deploys app.jar to s3"
	echo "  deployui    Runs spring boot app in api"
	echo "  upload      Uploads Artifacts s3"
	echo
	echo "Mock / Local Dev Targets:"
	echo "  mock       Runs dynambodb / s3 mocks in foreground"
	echo "  mockd      Runs dynambodb / s3 mocks in daemon mode"
	echo "  mockstop   Stops dynambodb / s3 mock docker containers"
#	echo "  json-server Runs json-server to mock rest api for ui"
	echo "  clean       Cleanup build / dist directories"
	echo

init: ; cd terraform; terraform init
plan: init
	 cd terraform; terraform plan
apply: ; cd terraform; terraform apply --auto-approve
dns: ; cd terraform; terraform apply -target=aws_route53_record.instance_dns --auto-approve

## ec2
ec2-stop: ; aws ec2 stop-instances --instance-ids $(shell grep "^instance_id" terraform/local/setenv.sh |cut -d= -f2)
ec2-start: ; aws ec2 start-instances --instance-ids $(shell grep "^instance_id" terraform/local/setenv.sh |cut -d= -f2)
ec2-status: ; aws ec2 describe-instances --instance-ids $(shell grep "^instance_id" terraform/local/setenv.sh |cut -d= -f2) --query 'Reservations[].Instances[].State[].Name' --output text
login: ; ssh -i mykey.pem -o StrictHostKeyChecking=no ec2-user@$(shell grep "^public_ip" terraform/local/setenv.sh |cut -d= -f2)
ssh: login ##alias

## builds
jardev: ; gradle clean bootJar; ls -l build/libs/app.jar
jarprod: ; gradle -Pprod clean bootJar
jarrun: ; java -Dspring.profiles.active=prod,localstack -jar  build/libs/app.jar
webdev: ; npm run webpack:build
webprod: ; npm run webpack:prod
## not target wildcars possible yet :-(
upload: ; cd terraform; terraform apply -target=aws_s3_bucket_object.appserviceenv \
          -target=aws_s3_bucket_object.appservice -target=aws_s3_bucket_object.installscript \
          -target=aws_s3_bucket_object.nginxconf -target=aws_s3_bucket_object.webapp \
          -target=aws_s3_bucket_object.bootjar --auto-approve

## Mocks
mock: ; docker-compose -f src/mock/docker-compose.yml up
mockd: ; docker-compose -f src/mock/docker-compose.yml up -d
mockstop: ; docker-compose -f src/mock/docker-compose.yml stop
# docker-run: ; docker run -p 8080:8080 --env-file local/env.list --name $(APPID) $(APPID):latest
#json-server: ; cd ui; ./mock.sh
deployui:
	$(AWS_CMD) s3 sync ui/dist/webapp s3://${S3_BUCKET_LOCATION}/deploy/webapp  --delete --size-only
	$(AWS_CMD) s3 cp ui/dist/webapp/index.html s3://${S3_BUCKET_LOCATION}/deploy//webapp/index.html
    ## size-only is not good for index.html as the size may not change but the checksum of included scripts does

deployjar:
	$(AWS_CMD) s3 sync api/build/libs/app.jar s3://${S3_BUCKET_LOCATION}/deploy/app.jar

#todo aws ec2 describe-instances --filters "Name=tag:Name,Values=MyInstance"
#  aws ec2 describe-instances --filters "Name=tag:appid,Values=$APPID" --query "Reservations[].Instances[].InstanceId"
clean:             ## Clean up (gradle + npm artifacts)
	rm -rf build
