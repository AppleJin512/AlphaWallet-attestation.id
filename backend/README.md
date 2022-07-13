# Backend of Attestation.id

This sub-project is the backend of attestation.id, providing restful services for its frontend.

## Cli

The final build includes a simple cli and its usage can be got with `help` subcommand, example:

```shell
java -jar build/libs/backend-0.1-all.jar help
```

Example output:

```shell
Usage: java -jar backend-<version>-all.jar subcommand.

Available subcommands:

    keys,       generate a key pair used by server.
    server,     kick off the server, which is default.
    help,       help of this program.

Start server: java -jar backend-<version>-all.jar
```

Enable OpenAPI:

```sh
# You should set micronaut environment to openapi
# You can do this by setting system property micronaut.environments
# or environment variable MICRONAUT_ENVIRONMENTS

# E.g:
MICRONAUT_ENVIRONMENTS=openapi ./gradlew run

# or
java -Dmicronaut.environments=openapi -jar backend-<version>-all.jar

# WebUI can be found in
# http://localhost:8080/swagger-ui/
# http://localhost:8080/redoc/
# http://localhost:8080/rapidoc/
```

## Restful API

### Ask for an attestation

- method: POST
- url: /api/attestation
- request: json format
  - validity: how many seconds the attestation should be valid
  - attestor: name of attestor
  - publicRequest: request data
  - headers content:
    - x-pap-ac: access token of this identifier
    - x-pap-id-provider: id provider of this identifier. E.g: `auth0`, `firebase`, and so on.
- response: json format
  - attestation: attestation value, BASE64-Encoded string
  - attestorPublicKey: public key of the attestor, BASE64-Encoded string

Example:

```shell
curl -X POST -H 'Content-Type: application/json' -H 'x-pap-ac: your_access_token' -H 'x-pap-id-provider: plugin_id' -i http://localhost:8080/api/attestation --data '{
"validity": 3600,
"attestor": "AlphaWallet",
"publicRequest": "{"signatureInHex":"0x72702adf76437214fa88c4880df06d1456d17dc9ebeab58fc75088c2573f1f114bf23359ff8be40dd76b170186229c1007d020a97bf48c30f23eeffb2bb79a121c","jsonSigned":"{\"types\":{\"EIP712Domain\":[{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"version\",\"type\":\"string\"}],\"AttestationRequest\":[{\"name\":\"payload\",\"type\":\"string\"},{\"name\":\"description\",\"type\":\"string\"},{\"name\":\"timestamp\",\"type\":\"string\"},{\"name\":\"identifier\",\"type\":\"string\"}]},\"primaryType\":\"AttestationRequest\",\"message\":{\"payload\":\"MIIBAgIBATCB_ARBBAmgnnZ6GqlmptON7vUlSzS1BiwkBq0FvyWHUvVaIrFHEhOuGkVN_w58auM_of7djTXgyDJ6_0Riy_vFOeQwusYEICUamcA97wgkmMxQGEByASCu-b31vz4bd8QxigAJBq1BBEEEKgWKKy5kkOXxDtO1T3nfnSDlPxsBexeQR8eQ4jcmKcMq6YdrXk3vl0wqEznyiSTLTuhiIuBBU0X17FGKxb-TEQRSMFgxNkYxQ0RGNTIwMEQ3QUU3RjA3QzE1MjJGMTkwNTJBNzIyRDkzOTcwqZU50y9kqXbiNUmNpswjqJjyXO1uD4eQiw8qoNwfgHwAAAF5NpzQjg\",\"description\":\"Linking Ethereum address to phone or email\",\"timestamp\":\"Tue May 04 2021 17:01:57 GMT+0800\",\"identifier\":\"jianhgreat@hotmail.com\"},\"domain\":{\"name\":\"http://wwww.attestation.id\",\"version\":\"0.1\"}}"}"}'
```

### Ask for a Public attestation

- method: POST
- url: /api/attestation/public
- request: json format
  - body content:
    - id: user twitter id
    - message: message for signing, hex encoded
    - signature: signature by user's eth wallet, hex encoded
    - ethAddress: address for signing this message
    - identifier: full twitter identifier, eg: https://twitter.com/foxgem
  - headers content:
    - x-pap-ac: access token of this identifier
    - x-pap-id-provider: id provider of this identifier. E.g: `auth0`, `firebase`, and so on.
- response: json format
  - attestation: attestation value, BASE64-Encoded string
  - attestorPublicKey: public key of the attestor, BASE64-Encoded string

Example:

```shell
curl -X POST -H 'Content-Type: application/json' -H 'x-pap-ac: your_access_token' -H 'x-pap-id-provider: plugin_id' -i http://localhost:8080/api/attestation/public --data '{"id":"14190486","identifier":"https://twitter.com/foxgem","message":"536f6d65207465787420666f72204175746f6772617068657220746f207369676e20736f2077652063616e2067656e657261746520746865206174746573746174696f6e","signature":"0xa31ab92f335062cee755257a8157fbef20d98abeb06ec3fd6975b2ab3018c9327daaf2d91fc4efdffcd10c2ddcca37f37ffecf16fb3910f6881b39bb99e1fcf01c","ethAddress":"0x16f1CdF5200d7ae7f07c1522f19052A722D93970"}'
```

### Ask for an UnSigned Nft attestation

- method: POST
- url: /api/attestation/nft
- request: json format
  - publicAttestation, BASE64-Encoded string
  - nfts: json array, for each item:
    - address: nft contract address
    - tokenId
- response: json format
  - attestation: attestation value, hex string
  - attestorPublicKey: public key of the attestor, BASE64-Encoded string

Example:

```shell
curl -X POST -H 'Content-Type: application/json' -i http://localhost:8080/api/attestation/nft --data '{
   "publicAttestation": "MIICEjCCAb+gAwIBEwIBATAJBgcqhkjOPQQCMBkxFzAVBgNVBAMMDmF0dGVzdGF0aW9uLmlkMCIYDzIwMjEwNTA2MDkyMjIzWhgPOTk5OTEyMzExNTU5NTlaMDQxMjAwBgkrBgEEAYF6ATkWI2h0dHBzOi8vdHdpdHRlci5jb20vZm94Z2VtIDE0MTkwNDg2MIIBMzCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////////////////////////////////////v///C8wRAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHBEEEeb5mfvncu6xVoGKVzocLBwKb/NstzijZWfKBWxb4F5hIOtp3JqPEZV2k+/wOEQio/Re0SKaFVBmcR9CP+xDUuAIhAP////////////////////66rtzmr0igO7/SXozQNkFBAgEBA0IABDNehahIPcRJHPe/te5LNC8ycKIGte/3rKC8WBv1eqkYJ3PoXls5ts4wK88VT3nSjZmqQwboqZ71ULFONTSJ5okwCQYHKoZIzj0EAgNCAKiaaPDFKhFI+/m2p8TLDiLE6ejHXOdNR2/SHwcHJ8YAdGZ9GiZyO1NrJYLNpd4CjP5xGPPbmggr0UTghVP6UbQc",
   "nfts":[{"address":"0xa567f5a165545fa2639bbda79991f105eadf8522", "tokenId":"1"}]
}'
```

### Ask for a Signed Nft attestation

- method: POST
- url: /api/attestation/signed-nft
- request: json format
  - publicKey, BASE64-Encoded string
  - nftAttestation, hex string
  - signature, signature of nftAttestation hex string
- response: json format
  - attestation: attestation value, hex string
  - attestorPublicKey: public key of the attestor, BASE64-Encoded string

Example:

```shell
curl -X POST -H 'Content-Type: application/json' -i http://localhost:8080/api/attestation/signed-nft --data '{
  "publicKey": "MIIBMzCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////////////////////////////////////v///C8wRAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHBEEEeb5mfvncu6xVoGKVzocLBwKb/NstzijZWfKBWxb4F5hIOtp3JqPEZV2k+/wOEQio/Re0SKaFVBmcR9CP+xDUuAIhAP////////////////////66rtzmr0igO7/SXozQNkFBAgEBA0IABDNehahIPcRJHPe/te5LNC8ycKIGte/3rKC8WBv1eqkYJ3PoXls5ts4wK88VT3nSjZmqQwboqZ71ULFONTSJ5ok=",
  "nftAttestation": "0x3082023330820212308201bfa003020113020101300906072a8648ce3d040230193117301506035504030c0e6174746573746174696f6e2e69643022180f32303231303530373036353132345a180f39393939313233313135353935395a30343132303006092b06010401817a0139162368747470733a2f2f747769747465722e636f6d2f666f7867656d203134313930343836308201333081ec06072a8648ce3d02013081e0020101302c06072a8648ce3d0101022100fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f3044042000000000000000000000000000000000000000000000000000000000000000000420000000000000000000000000000000000000000000000000000000000000000704410479be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8022100fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd036414102010103420004335e85a8483dc4491cf7bfb5ee4b342f3270a206b5eff7aca0bc581bf57aa9182773e85e5b39b6ce302bcf154f79d28d99aa4306e8a99ef550b14e353489e689300906072a8648ce3d040203420011c38fa7fee773a2dbe4f621a8ef08d5e85ae9fa2fe5dc15b0d213c7659e5b8066ad43d9d93be467ae3063eb71123582342f1852150d8304aeb1343b61e3b0f61b301b30190414a567f5a165545fa2639bbda79991f105eadf8522040101",
  "signature": "0x793bbab3ead8cd2e330517c13ededf089e4d7ec55f127839403081a9c8ae11402a6727d2f1be06f9c73dbdedfa544746d196e421c4d53becd19abbcb27666b261b"
}'
```

### Ask for a CoSigned Identifier attestation

- method: POST
- url: /api/attestation/cosigned
- request: json format
  - publicKey, BASE64-Encoded string
  - publicAttestation, BASE64-Encoded string
  - signature, signature of publicAttestation hex string
- response: json format
  - attestation: attestation value, hex string
  - attestorPublicKey: public key of the attestor, BASE64-Encoded string

Example:

```shell
curl -X POST -H 'Content-Type: application/json' -i http://localhost:8080/api/attestation/cosigned --data '{
  "publicAttestation": "MIICFDCCAcGgAwIBEwIBATAJBgcqhkjOPQQCMBkxFzAVBgNVBAMMDmF0dGVzdGF0aW9uLmlkMCIYDzIwMjExMDEzMDk0MDA3WhgPOTk5OTEyMzExNTU5NTlaMDYxNDAyBgkrBgEEAYF6ATkMJWh0dHBzOi8vdHdpdHRlci5jb20vMTQxOTA0ODYgMTQxOTA0ODYwggEzMIHsBgcqhkjOPQIBMIHgAgEBMCwGByqGSM49AQECIQD////////////////////////////////////+///8LzBEBCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAcEQQR5vmZ++dy7rFWgYpXOhwsHApv82y3OKNlZ8oFbFvgXmEg62ncmo8RlXaT7/A4RCKj9F7RIpoVUGZxH0I/7ENS4AiEA/////////////////////rqu3OavSKA7v9JejNA2QUECAQEDQgAEM16FqEg9xEkc97+17ks0LzJwoga17/esoLxYG/V6qRgnc+heWzm2zjArzxVPedKNmapDBuipnvVQsU41NInmiTAJBgcqhkjOPQQCA0IAJp2Py2PmoQiUxh9FZ9LrgKHy64M8XYxzaCE8GYKLF4VvzBcCKLNlxl+m++vSgXHqWKutci6c+AV4Jk+si4DtUhw=",
  "publicKey": "MIIBMzCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////////////////////////////////////v///C8wRAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHBEEEeb5mfvncu6xVoGKVzocLBwKb/NstzijZWfKBWxb4F5hIOtp3JqPEZV2k+/wOEQio/Re0SKaFVBmcR9CP+xDUuAIhAP////////////////////66rtzmr0igO7/SXozQNkFBAgEBA0IABDNehahIPcRJHPe/te5LNC8ycKIGte/3rKC8WBv1eqkYJ3PoXls5ts4wK88VT3nSjZmqQwboqZ71ULFONTSJ5ok=",
  "signature": "0x1a345af6b0ddb778c77c92b04258264206f9bb6b234ede3dd840c74a5d6bb8be0100b53a1a683060a42bbcf9e64691c688f0d6b7855ea44a13c9381ab7a31dc11c"
}'
```

In [ApiControllerSpec.groovy](src/test/groovy/id/attestation/ApiControllerSpec.groovy), you can find all code examples of the RESTful API endpoints.

## How to run

Be sure JDK 11 is installed on your computer before running.

### Setup Enviroment variables

- Log Level

  ```sh
  # valid: TRACE, DEBUG, INFO, WARN, ERROR, default: DEBUG
  export LOG_LEVEL=INFO
  ```

- EMail Service

  - option 1, AWS SES

  ```shell
  export AWS_REGION=XXXXXXXX
  export AWS_SOURCE_EMAIL=attestor@attestation.id
  export AWS_ACCESS_KEY_ID=XXXXXXXX
  export AWS_SECRET_KEY=XXXXXXXX
  ```

  - option 2, SendGrid

  ```shell
  export SENDGRID_FROM_EMAIL=attestor@attestation.id
  export SENDGRID_APIKEY=XXXXXX
  ./gradlew run
  ```

- Auth0 Service

  ```shell
  export AUTH0_DOMAIN=XXX
  export AUTH0_CLIENT_ID=XXX
  export AUTH0_CLIENT_SECRET=XXX
  ```

- Attestor PrivateKey (NOTE: **THIS IS FOR TESTING ONLY**), its value can be got with `keys` subcommand.

  ```shell
  export ATTESTOR_PRIVATE_KEY=MIICSwIBADCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////////////////////////////////////v///C8wRAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHBEEEeb5mfvncu6xVoGKVzocLBwKb/NstzijZWfKBWxb4F5hIOtp3JqPEZV2k+/wOEQio/Re0SKaFVBmcR9CP+xDUuAIhAP////////////////////66rtzmr0igO7/SXozQNkFBAgEBBIIBVTCCAVECAQEEIP0NZmYQRZgBrndcBR6RUJdEfBaQoF+/yUNQHpEM7s7GoIHjMIHgAgEBMCwGByqGSM49AQECIQD////////////////////////////////////+///8LzBEBCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAcEQQR5vmZ++dy7rFWgYpXOhwsHApv82y3OKNlZ8oFbFvgXmEg62ncmo8RlXaT7/A4RCKj9F7RIpoVUGZxH0I/7ENS4AiEA/////////////////////rqu3OavSKA7v9JejNA2QUECAQGhRANCAASs39hcmVWwLtOaxxgp26vuszAncsReQC1Rjb6K7IixLQESpfQ1cNaqivTzDYTgzz1cQ4GswxUvF7HOHaqL0gyP
  ```

### Kickoff server

NOTE: Under development mode, if you have a problem to connect google server, please `export MICRONAUT_ENVIRONMENTS=dev` and change the proxy in `application-dev.yml`.

- with `gradle`

```shell
./gradlew run
```

or

```shell
./gradlew run --args="server"
```

For other subcommands, replace the `server` with the subcommand to be run.

- with `java`

```shell
java -jar build/libs/backend-0.1-all.jar
```

or

```shell
java -jar build/libs/backend-0.1-all.jar server
```

For other subcommands, replace the `server` with the subcommand to be run.

### Run testing

For testing, run with (NOTE: with a clean env):

```shell
./gradlew test
```

### Run locally

```sh
./gradlew run
```

This will run backend with all plugins locally. You must declare all required environment variables before.

E.g:

```sh
# required by auth0 plugin
export AUTH0_DOMAIN=xxx AUTH0_CLIENT_ID=xxx AUTH0_CLIENT_SECRET=xxx

# required by firebase plugin
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/firebase-adminsdk.json

./gradlew run
```

### Build a fatjar

For fatjar, run with:

```shell
# build only fat jar
./gradlew shadowJar

# build fat jar & shadow zip & dist zip
./gradlew assemble
```

The fatjar can be found in `build/libs/`.

## References

- [Send Emails from Micronaut](https://guides.micronaut.io/micronaut-email/guide/index.html)
- [Error Handling](https://guides.micronaut.io/micronaut-error-handling/guide/index.html)
- [Micronaut Test - Testing with Spock](https://micronaut-projects.github.io/micronaut-test/latest/guide/#spock)
