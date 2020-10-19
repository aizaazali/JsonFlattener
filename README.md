# JsonFlattener

This repo contains a program that takes a JSON object as input and outputs a flattened version of the JSON object, with keys as the path to every terminal value in the JSON structure. 

For example, consider the following JSON object: 

```json
{
    "a": 1,
    "b": true,
    "c": {
        "d": 3,
        "e": "test"
    }
}
```

In this example the path to the terminal value `1` is `"a"` and the path to the terminal value `3` is `"c.d"`.

The output for the above object would be:

```json
{
    "a": 1,
    "b": true,
    "c.d": 3,
    "c.e": "test"
}
```

Output should be valid JSON.

## Assumptions

* The input is a JSON object
* All keys named in the original object will be simple strings without ‘.’ characters
* The input JSON will not contain arrays
* A library can be used to parse JSON from a string to an object

## Requirements:
* Output should be valid JSON
* Command line should correspond to linux conventions, eg using pipes `cat test.json | mycode` 

---

## Implementation:

The JsonParser performs the following steps:

- Read the json input as a string
- Flatten the json and store the path to terminal values(as keys) and the terminal values(as values) in a map
- Create a json object from this map
- Print the result

## Validation:

The JsonParserTest contains 18 test cases that thoroughly validate the JsonParser. In addition to it this has also been verified by passing several inputs through command line

```json
Aizaaz-Air:src aizaaz$ cat test.json
{
    "a": 1,
    "b": true,
    "c": {
        "d": 3,
        "e": "test"
	}
    }
}
Aizaaz-Air:src aizaaz$ cat test.json | java JsonParser
{
	"a":1,
	"b":true,
	"c.d":3,
	"c.e":"test"
}

For invalid input:

Aizaaz-Air:src aizaaz$ cat test1.json
{
"name":"John",
"age":30,
"cars":[ "Ford", "BMW", "Fiat" ]
}
Aizaaz-Air:src aizaaz$ cat test1.json | java JsonParser
The json object is not valid java.lang.IllegalArgumentException: Invalid input: ["Ford","BMW","Fiat"]
```
