# DataLoader
Para compilar:

javac -d . MTCom.java
javac -d . Register.java
javac -d . Ident.java
javac -d . Trf.java
javac -d . Map.java
javac -d . Design.java
javac -d . Proc.java
javac -d . Reader.java
javac -d . -classpath .:ruta_a/jars/solr-solrj
javac -d . DataLoader.java
jar -cvf dataloader.0.92.jar ./org/dataloader/*.class

