# Links

[Swagger UI](http://localhost:8080/swagger-ui/index.html) - Swagger UI only hosted in gateway.
Proxies to backend services.

# Authentication
Send a HTTP post to the gateway authentication endpoint:

```
http POST http://localhost:8080/api/authenticate username=admin password=admin
```

Response:
```
HTTP/1.1 200 OK
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTQ4NDU4NTUyNn0.Ydhn5boMV5jGqjd_hSCLU6dGuddghZOQCjrt7--Boc3upUgyZ9LEQR_OhK90vG4jdCencwmJyq4pOXRrVuWyuQ
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Type: application/json;charset=UTF-8
Date: Sun, 15 Jan 2017 16:52:07 GMT
Expires: 0
Pragma: no-cache
Transfer-Encoding: chunked
X-Application-Context: gateway:swagger,dev:8080
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block

{
    "id_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTQ4NDU4NTUyNn0.Ydhn5boMV5jGqjd_hSCLU6dGuddghZOQCjrt7--Boc3upUgyZ9LEQR_OhK90vG4jdCencwmJyq4pOXRrVuWyuQ"
}
```

Using the id_token response we can use this as an Authorization Header. For example, using HTTPie. 

```
http http://localhost:8080/foo/api/some-entities \
"Authorization:Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTQ4NDU4NTUyNn0.Ydhn5boMV5jGqjd_hSCLU6dGuddghZOQCjrt7--Boc3upUgyZ9LEQR_OhK90vG4jdCencwmJyq4pOXRrVuWyuQ"
```
