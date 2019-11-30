#!/usr/bin/env bash
export AWS_PROFILE=yummy
aws dynamodb --endpoint-url http://localhost:8000 list-tables
for T in place region; do
   # aws dynamodb scan --limit 25 --table-name yummy-$T |  jq '{"letsgo2-$T": [.Items[] | {PutRequest: {Item: .}}]}' >letsgo2-$T.json
    echo "Export file $(ls -l samples/letsgo2-$T.json)"
    aws dynamodb --endpoint-url http://localhost:8000 create-table --table-name letsgo2-$T --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1
    sleep 2
    aws dynamodb --endpoint-url http://localhost:8000 batch-write-item --request-items file://samples/letsgo2-$T.json
    aws dynamodb --endpoint-url http://localhost:8000 scan --table-name letsgo2-$T

done
