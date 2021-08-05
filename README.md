# :boom: FastMongo v2 [![BCH compliance](https://bettercodehub.com/edge/badge/360matt/FastMongo-v2?branch=master)](https://bettercodehub.com/)

ORM Allowing to modify documents in the Mongo db very quickly and easily with class fields and optimized reflection.

### :interrobang: Why use this API ?
* :bulb: As simple as possible, it is easy to learn
* :hourglass: Its use is very fast, even the migration
* :art: It is customizable, you can define many behavior in this API
* :floppy_disk: Your data is better structured
* :vertical_traffic_light: You can develop your project and your structure
* :recycle: Very light, my code represents only 11 KB (against 17 KB in v1)

### :fire: What's new in v2 ?
* :hourglass: Better performance
* :hourglass: Better reflection system (with cache)
* :recycle: Less class, less instantiation
* :recycle: Generic Type removed
* :recycle: Less code needed, much simpler
* :busts_in_silhouette: Can now use multiple (infinite?) database at the same time

### :link: Dependencies:
* I only use the official Mongo driver in the latest version (4.2.3)

## Maven
```
<dependency>
    <groupId>io.github.360matt</groupId>
    <artifactId>FastMongo-v2</artifactId>
    <version>2.0-SNAPSHOT</version>
</dependency>
```

### :muscle: Benchmarks:
With FX8300 overclocked 4Ghz / DDR3 overclocked 1600Mhz.  
With 500_000 documents.  
Action: load (doc->field), modify (field) and save (field->doc).  

```
Time elapsed: 160ms.
Every document: 0,000318ms (= 318ns)
```



## :star: Beginning:
1. You must define the connection to the database:
```java

// with url
Database database = new Datebase( "database", "url connection" );

// ________________________________________________________________

Database.Auth auth = new Database.Auth();
auth.database = "";
auth.host = "";
auth.password = "";
auth.port = 0;
auth.user = "";

// with Auth instance.
Database database = new Datebase( auth );
```
2. Methods:
```java
database.close();
// close your database connection.

database.setDefault();
// Used to define this database as default.
// The first instantiation is already defined as default.

```

## :zzz: Legacy references:
```java
Database#getMongoClient() // MongoClient
Database#getMongoDatabase() // MongoDatabase
Manager#getMongoCollection() // MongoCollection<Document>
```

## :zap: Collection Manager:
```java
Manager man = new Manager( "name" );
// get collection Manager for default (first instantied) database


Manager man = new Manager ( "name", Database );
// get collection Manager for chosen database


```

### :hammer: Features:
* Modify default field id name:  
```java
man.setFieldID( "_id" );
// Define the name of the default field used by the API when instantiating data classes.
```

* Clear/Drop a collection:  
```java
man.drop();
```

* Check if document exist by id:  
```java
booelan state = man.existObject( "name" );
```

* Remove a document by id staticly:  
```java
man.remove( "name" );
```

* It is possible to classify documents by order according to one or more fields:  
```java
Sort sort = man.buildSort("", "", "", "", "", ""); // you can set ascending fields here
Sort sort = man.buildSort(); // or keep empty



sort.ascending("one", "two", "variadic");
sort.descending("three", "fourth", "variadic");

sort.skip(10); // ignore the 10 firsts documents
sort.setLimit(50); // maximum 50 documents returned
```

You can return documents:
```java
List<Document> list = sort.getDocuments();
```

You can return instances:
```java
Function<Document, ****> instantiate = (doc) -> {
  return new ****(doc);
};
// this function permit to instantiate your data-class instances.
// You must declare your own constructor, call it ``super(document, manager);``

// ___________________________________________________________________________

List<****> list = sort.getStructure(instantiate);

```

You can forEach instances:
```java
Function<Document, ****> instantiate = (doc) -> {
  return new ****(doc);
};
// this function permit to instantiate your data-class instances.
// You must declare your own constructor, call it ``super(document, manager);``

// ___________________________________________________________________________

sort.foreachStructure(instantiate, (yourInstances of ****) -> {
    // do stuff
});


```

## :unlock: Yours Class-Data instances (represents a document):
The instances each represent a document whether it is fictitious or not.  
it is thanks to an instance that we can handle a document in the DB (create, modify, delete) 

Example blank class-file:
```java
public class **** extends Structure {
    public static final Manager manager = new Manager("****");
    
    public **** (final Object id) {
        super(id, manager);
    }

    public **** (final Document doc) {
        super(doc, manager);
    }
}
```


### Features:
* Get current document equivalent:
```java
**** instance = ?;

Document doc = instance.fieldToDoc(); 
// must catch java.lang.IllegalAccessException
```
* Set from document equivalent:
```java
**** instance = ?;

instance.docToField( doc ); 
// must catch java.lang.IllegalAccessException
```

### Extra:

## :innocent: For more help:
* You can add me on Discord: Matteow#6953

### :ghost: About Me:
* I am a 17 year old French developer.
* I started around 12 years old with basic PHP, then around 14 years old I was doing Discord bots in JS with NodeJS.
* I finally started Java around 15 (so it's been over a year), and my experience has improved over time (see the rest of my Github) 
