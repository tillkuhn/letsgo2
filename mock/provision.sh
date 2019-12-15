#!/usr/bin/env bash
export AWS_PROFILE=timafe
PREFIX="letsgo2-"

for TC in place:id region:code; do
    T=$(echo $TC|cut -d':' -f 1)
    C=$(echo $TC|cut -d':' -f 2)
   # aws dynamodb scan --limit 25 --table-name yummy-$T |  jq '{"letsgo2-$T": [.Items[] | {PutRequest: {Item: .}}]}' >letsgo2-$T.json
    if ! aws dynamodb --endpoint-url http://localhost:8000 list-tables|grep -q $PREFIX$T; then
        echo "Dynamodb Table $PREFIX$T does not exist "
        aws dynamodb --endpoint-url http://localhost:8000 create-table --table-name $PREFIX$T --attribute-definitions AttributeName=$C,AttributeType=S --key-schema AttributeName=$C,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
        sleep 2
    else
        echo "Dynamodb Table $PREFIX$T already exists, skip creation "
    fi
    IMPORTFILE="$( dirname "${BASH_SOURCE[0]}" )/samples/$PREFIX$T.json"
    echo "Importing from dumpfile $IMPORTFILE to Dynamodb Table "
    aws dynamodb --endpoint-url http://localhost:8000 batch-write-item --request-items file://$IMPORTFILE
    echo "Dynamodb Table $PREFIX$T now contains $(aws dynamodb --endpoint-url http://localhost:8000 scan --table-name $PREFIX$T | grep "\"$C\"" |wc -l) records"
done
# AWS_PROFILE=timafe aws dynamodb --endpoint-url http://localhost:8000 batch-write-item --request-items file://samples/letsgo2-$T.json
# AWS_PROFILE=timafe aws dynamodb --endpoint-url http://localhost:8000 scan --table-name letsgo2-$T
# AWS_PROFILE=timafe aws dynamodb --endpoint-url http://localhost:8000  delete-table --table-name letsgo2-region
# AWS_PROFILE=timafe aws dynamodb --endpoint-url http://localhost:8000 create-table --table-name letsgo2-region --attribute-definitions AttributeName=code,AttributeType=S --key-schema AttributeName=code,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
