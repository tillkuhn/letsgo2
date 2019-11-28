### Certbot + auto security updates

* [Certbot Renewal](https://www.jhipster.tech/production/) Configure auto-renewal of SSL certificates: add 10 3 * * * /usr/bin/certbot renew --quiet in your crontab
*  yum update --security -y
* [cron d entries](https://stackoverflow.com/questions/878600/how-to-create-a-cron-job-using-bash-automatically-without-the-interactive-editor)
### when lauch user-data?
[How can I execute user data to automatically create a file with every restart of my Amazon EC2 instance?](https://aws.amazon.com/de/premiumsupport/knowledge-center/execute-user-data-ec2/)
By default, user data scripts and cloud-init directives run only during the first boot cycle when an instance is launched. However, you can configure your user data script and cloud-init directives with a mime multi-part file. A mime multi-part file allows your script to override how frequently user data is executed in the cloud-init package. Then, the file executes the user script.

### Useful commands
```
   curl http://169.254.169.254/latest/user-data
   cat cat /var/log/cloud-init-output.log
```

## Terraform

* [How to create reusable infrastructure with Terraform modules](https://blog.gruntwork.io/how-to-create-reusable-infrastructure-with-terraform-modules-25526d65f73d)
* [Multiple Instances of a Module](https://www.terraform.io/docs/configuration/modules.html#multiple-instances-of-a-module)
* Run only for specific module 
```
AWS_PROFILE=xxx terraform apply -auto-approve  -target=module.dynamod
```

### JHipster production optimization

* [read](https://www.jhipster.tech/production/)
* [using nginx](https://www.jhipster.tech/separating-front-end-and-api/)

## Oauth redirect issues if running behinds ssl terminating LB
* Hard to debug: [Add logging in oauth2 modules](https://github.com/spring-projects/spring-security/issues/5262)
* [invalid_redirect_uri_parameter exception](https://github.com/spring-projects/spring-security-oauth/issues/1344)
* [invalid_redirect_uri_parameter exception while running microservices on separate machines](https://github.com/spring-projects/spring-security/issues/5270)
* [Running Behind a Front-end Proxy Server](https://docs.spring.io/spring-boot/docs/2.0.2.RELEASE/reference/htmlsingle/#howto-use-tomcat-behind-a-proxy-server)
* [How To Set Up Nginx Load Balancing with SSL Termination](https://www.digitalocean.com/community/tutorials/how-to-set-up-nginx-load-balancing-with-ssl-termination)

Check 
```
sudo vi /etc/nginx/nginx.conf
sudo vi /etc/systemd/system/letsgo2.service
sudo systemctl daemon-reload
sudo systemctl restart letsgo2
sudo systemctl restart nginx
```

Bottom line: use SERVER_USE_FORWARD_HEADERS=true for spring boot and the following config in nginx
```
    location / {
        proxy_pass http://mywebapp1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
```
