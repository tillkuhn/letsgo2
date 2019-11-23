### Certbot

* [Certbot Renewal](https://www.jhipster.tech/production/) Configure auto-renewal of SSL certificates: add 10 3 * * * /usr/bin/certbot renew --quiet in your crontab

### Useful
```
   curl http://169.254.169.254/latest/user-data
   cat cat /var/log/cloud-init-output.log
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
