API for Certifoto
=================

## create photo records

url: /record

method: POST

request body: (JSON)

````json
{
	m: "mobile phone id",
	f: "MD5 of photo",
	p: "encrypted position"
	t: "encrypted local photo-taken time in ms"
}
````

return body:
````json
{
	err: 0 no error, > 1, error code
	id: "photo ID",
	msg: "extra message, will be shown to user"
}
````


## upload photo

url: /upload

method: POST

request body: (JSON)
````json
{
	m: "mobile phone id"
	id: "photo id",
	c: "base64 encrypted content"
	t: "encrypted local photo taken time in ms"
}
````

response body: 
````json
{
	err: 0 no error, > 1, error code
	id: "photo ID",
	msg: "extra message, will be shown to user"
}
````


