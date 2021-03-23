all : 
	sbt assembly

test: all

run : all
	java -jar target/unitconverter.jar

docker: all
	docker build -t unitconverter:0.1 .

run-docker: docker
	docker run -p 8080:8080 unitconverter:0.1
