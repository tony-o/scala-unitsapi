# unit converter

A quick unit converter api written in scala 2.13 (only version tested).

This api uses an homegrown LR parser to generate _an_ answer. The base package `com.tonyo.Calculator` can be expanded to add/remove/change things and make this a fully working calculator if desired (eg `t * 5 => (5000, "kg * 5")`)

## Usage

build & test (all of the subsequent commands do this same action)

```
make # sbt assembly
```

run locally

```
make run # sbt assembly
         # java -jar target/unitconverter.jar
```

build docker

```
λ ~$ make docker # sbt assembly
                 # docker build -t unitconverter:VERSION .
# ...
λ ~$ docker image ls
REPOSITORY                 TAG             IMAGE ID       CREATED              SIZE
unitconverter              0.1             3478d7d4a1b8   About a minute ago   559MB
```

run docker after build:

```
λ ~$ make run-docker # sbt assembly
                     # docker build -t unitconverter:VERSION .
                     # docker run -p 8080:8080 unitconverter:VERSION
# logging info
```
```
λ ~$ curl -s ':8080/units/si?units=("/º)' | jq
# 200 OK
{
  "multiplication_factor": 0.0002777777777777778,
  "unit_name": "(rad/rad)"
}
λ ~$ curl -s ':8080/units/si?units=y*t' | jq
# 422 Unprocessable Entity
{
  "error": "Token 'y' is unknown"
}
```
