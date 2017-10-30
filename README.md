# Fort

Small example to get a feel for Vault, AppRole's and scala-vault

The idea is to give the developer some freedom. If dev wants to use her own values for specific secrets, she can export certain environment variables or just rewrite the `application.conf`. If a secret does not exist, the config will need to contact Vault, and then relies on `VAULT_ADDR` and `TOKEN` being setup as env variables.

```
# start vault in background
vault server -dev &

# dev server doesn't have tls
export VAULT_ADDR='http://127.0.0.1:8200'

# only cli is pre-authenticated, create new token for api access
vault token-create --format json | jq .auth.client_token

# export that token as environment variable
export TOKEN=<TOKEN GOES HERE>

# write secret
vault write secret/service/app password=mypassword

# run the example
sbt run
```
