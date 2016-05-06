#!/bin/sh

#constantes
OUTPUT_FOLDER="ca-ws/src/main/java/pt/upa/ca/keys"
CA_FOLDER="$OUTPUT_FOLDER/ca"
CA_PEM_FILE="$CA_FOLDER/ca-certificate.pem.txt"
CA_KEY_FILE="$CA_FOLDER/ca-key.pem.txt"

#ca
rm -r $OUTPUT_FOLDER
mkdir $OUTPUT_FOLDER
mkdir $CA_FOLDER
echo "A gerar chaves pub e privadas da ca..."
openssl genpkey -algorithm RSA -out $CA_FOLDER/private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in $CA_FOLDER/private_key.pem -out $CA_FOLDER/public_key.pem
echo "Chaves geradas"

rm -r broker-ws/src/main/java/pt/upa/broker/keys
rm -r transporter-ws/src/main/java/pt/upa/transporter/keys
mkdir broker-ws/src/main/java/pt/upa/broker/keys
mkdir broker-ws/src/main/java/pt/upa/broker/keys/ca  

mkdir transporter-ws/src/main/java/pt/upa/transporter/keys
mkdir transporter-ws/src/main/java/pt/upa/transporter/keys/ca
  
#servidores broker e transporter
for server_name in $*
do
  key_folder=keys
  if [ "$server_name" = "broker" ]
  then
	path=broker-ws/src/main/java/pt/upa/broker
  else
	path=transporter-ws/src/main/java/pt/upa/transporter
  fi
  mkdir $path/$key_folder/$server_name
  echo "A gerar par de chaves de $server_name..."
  openssl genpkey -algorithm RSA -out $path/$key_folder/$server_name/private_key.pem -pkeyopt rsa_keygen_bits:2048
  echo "A colocar chaves pub de $server_name na ca..."
  mkdir $OUTPUT_FOLDER/$server_name
  openssl rsa -pubout -in $path/$key_folder/$server_name/private_key.pem -out $OUTPUT_FOLDER/$server_name/public_key.pem
  echo "A colocar a chave pública da ca no $server_name..."
  cp $CA_FOLDER/public_key.pem $path/$key_folder/ca
  
done



