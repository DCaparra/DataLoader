cd ./src/
javac -d . MTCom.java
javac -d . Register.java
javac -d . Ident.java
javac -d . Trf.java
javac -d . Map.java
javac -d . Design.java
javac -d . Proc.java
javac -d . Reader.java
javac -d . -classpath .:/home/diego/Preproceso/DataLoader/Final/jars/solr-solrj-6.1.0.jar SolrInsert.java
javac -d . DataLoader.java
jar -cvf dataloader.1.00.jar ./org/dataloader/*.class
cd ..
rm ./api/dataloader.1.00.jar
cp ./src/dataloader.1.00.jar ./api/dataloader.1.00.jar
rm ./src/dataloader.1.00.jar

