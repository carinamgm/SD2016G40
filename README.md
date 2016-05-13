# Projeto de Sistemas Distribuídos 2015-2016 #

**Grupo de SD 40 - Prof. Miguel Pardal - Campus Alameda**

João Santos - 67011 - phoenix-2000@hotmail.com

Diogo Ferreira - 79018 - diogo.lopes.ferreira@tecnico.ulisboa.pt

Carina Martins - 79153 - carinamartins@outlook.com


Repositório:
[tecnico-distsys/A_40-project](https://github.com/tecnico-distsys/A_40-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

**[0]** Iniciar sistema operativo

Linux


**[1]** Iniciar servidores de apoio
```
JUDDI:
Ir à pasta juddi-.../bin
...
Na linha de comandos, executar o seguinte comando: ./startup.sh
```

**[2]** Criar pasta temporária

```
mkdir SD_R2_A_40
cd SD_R2_A_40
```


**[3]** Obter código fonte do projeto (segunda versão entregue)

```
$ git clone -b SD_R2 https://github.com/tecnico-distsys/A_40-project/
```



**[4]** Instalar módulos de bibliotecas auxiliares

```
cd uddi-naming
mvn clean install
```

### CA

**[5]** Correr a CA

```
cd ca-ws
mvn clean install
mvn exec:java
```


```
cd ca-ws-cli
mvn install
```

### Handlers

**[6]** Instalar os handlers criados, assegurando que o processo de ca-ws continua aberto


```
cd ws-handlers
mvn install
```

### Transporters

**[7]** Construir e executar o(s) servidor(es) do **Transporter**


```
cd transporter-ws
mvn install
mvn clean compile -Dws.i=$ exec:java

(onde $ é o último dígito do porto)
```

### Brokers

**[8]** Construir e executar os servidores do **Broker**, assegurando que os processos do transporter-ws continuam abertos

##### Broker Secundário

```
cd broker-ws
mvn install
mvn clean compile -Dws.i=4 -Dws.name=UpaBrokerSec exec:java
```

##### Broker Primário

```
cd broker-ws
mvn install
mvn clean compile exec:java
```

### Clientes

**[9]** Instalar os clientes, assegurando que os processos do transporter-ws e broker-ws continuam abertos

##### Broker-Cli

```
cd broker-ws-cli
mvn install
```

##### Transporter-Cli

```
cd transporter-ws-cli
mvn install
```



-------------------------------------------------------------------------------
**FIM**
