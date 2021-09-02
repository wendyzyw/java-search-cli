# java-search-cli
An interactive search application via command line to facilitate real-time searching of users and tickets from specified json file sources 

## Usage
### 1. git clone the project
```bash
git clone git@github.com:wendyzyw/java-search-cli.git
```
### 2. cd to the repo directory, build the application 
```bash
gradlew build
```
### 3. run the application 
```bash
gradlew run --console=plain
```

## Some Assumptions Made
1. search terms and search values are not case sensitive
2. searching on date field (etc. created_at) works the same as text search by supporting only ISO formatted date string or it's partial, etc. 2012-12-30
3. after search results are displayed to the users, it will automatically prompt for either exiting the system or re-starting another round of search
4. data from either json files are not normalized, which mean they would have heterogeneous fields which some of them have while others do not
5. ticket might have no assignee_id or have an assignee with id that is not contained in the user data file, in both cases the assignee_name will be blank in the search result 

## Some tradeoffs regarding the implementation 
1. in order to acquire an exhaustive set of all distinct fields, the application loops through all user/ticket json object to retrieve the fields 
2. for simplicity of the search, all search values are treated as string based input using java basic string comparison and contains method 
3. due to the nature of interactivity of the application, the parsing of the json data source file and the search for values are in different stages of the application, although with searching while parsing at the same time might be better in terms of processing time 
4. once application has started, before the user exists the system or any external interruptions arise, any changes/updates to the data source json file will not be reflected in the search results as the json data source files are parsed once at the initialization stage of the application
