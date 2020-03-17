#!/usr/bin/env python3

from wsgiref.simple_server import make_server
from datetime import datetime

def hello_world_app(environ, start_response):
    status = '200 OK'
    headers = [('Content-type', 'text/plain; charset=utf-8')]
    start_response(status, headers)
    text = 'Hello World @ {}'.format(datetime.now())
    return [text.encode('utf-8')]

httpd = make_server('', 8000, hello_world_app)
httpd.serve_forever()
