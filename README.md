# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 40 - Campus Alameda

João Santos 67011 phoenix-2000@hotmail.com

Diogo Ferreira 79018 diogo.lopes.ferreira@tecnico.ulisboa.pt

Carina Martins 79153 carinamartins@outlook.com

Repositório:
[tecnico-distsys/A_40-project](https://github.com/tecnico-distsys/A_40-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 


### Ambiente

[0] Iniciar sistema operativo

Linux


[1] Iniciar servidores de apoio
```
JUDDI:
Ir à pasta juddi-.../bin
...
Na linha de comandos, executar o seguinte comando: ./startup.sh
```

[2] Criar pasta temporária

```
cd ...
mkdir ...
```


[3] Obter código fonte do projeto (versão entregue)

```
$ git clone -b SD_R1 https://github.com/tecnico-distsys/A_40-project/
```



[4] Instalar módulos de bibliotecas auxiliares

```
cd uddi-naming
mvn clean install
```

### Servidores e testes unitários

[5] Construir e executar o servidor do **Transporter** com testes

```
cd transporter-ws
mvn clean install
mvn exec:java
```

[6] Construir e executar o servidor do **Broker** com testes

```
cd broker-ws
mvn clean install
mvn exec:java
```
### Clientes e testes de integração

[7] Construir o cliente do **Broker** e executar testes


```
cd broker-ws-cli
mvn clean install
```

[8] Construir o cliente do **Transporter** e executar testes

```
cd transporter-ws-cli
mvn clean install
```

...

-------------------------------------------------------------------------------
**FIM**
