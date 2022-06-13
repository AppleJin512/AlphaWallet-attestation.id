# Frontend of Attestation.id

This sub-project is the frontend of attestation.id, providing frontend pages.

## Prerequisite

Before running or building this project, enviorment variables needed by project should be set first:

- First, please create a .`env` file in the root of project.
- Set the values of variables and all variables:
  - BASE_BACKEND_URL, the backend of this project
  - VALIDITY, validity of attestation, unit is `second`
  - ATTESTOR, issuer of attestation

Here is an example of `.env`:

```
BASE_BACKEND_URL=https://backend-stage.attestation.id
VALIDITY=3600
ATTESTOR=AlphaWallet
```

Note: the key above is for testing only.

## Running

Run in your local development environment:

```bash
cd frontend
npm install
npm run dev
```

Then go to `http://localhost:5000`.

## Unit Testing

Run unit tests:

```bash
npm run test
```

## Deployment

Build package for deployment:

```bash
npm run build
```

Please remember to change the enviroment variables before building.

## Intergation

attestation.id can be integrated in a 3rd web site and there is an example for this, check [this link](public/iframe-example-sdk.html).

Note: for developer who wants to debug this page with the local server, please change:

- `attestationSite` in `attestor.js`
- `src` of \<script\> in `iframe-example-sdk.html`

### How to integrate

The usage is simple, with two steps:

1. include script, such as:

```js
<script src="https://stage.attestation.id/assets/javascripts/attestor.js"></script>
```

2. get values when it is ready with `Attestor.onReady(func, options)`

```js
  Attestor.onReady(function (data) {
    console.log(data.attestation);
    console.log(data.requestSecret);

    ...
  });
```

As for `options`, there are several optional items:

- force: default is `false`. When it is true, an iframe will be shown even `attestation + secret` saved in local storage. It will be useful when caller wants to reapply attestation anyway.
- container: the id of DOM element which will be the parent of `Application Page` which is shown when no local attestation existing. This item is useful when caller wants to control the way to show it.
- providerName: the default provider used by web3Modal, it's value needs to be consistent with the name of the provider available in web3Modal.
- email: the default email recevied the OTP and signed the attestation. If set this option, this email will autofill into the input in the Email Page and autosend the OTP to this email.
- debug: default is `false`. When it is true, there will a log `attestation.id postMessage data received:***` in the browser console.

To use these options:

```js
  Attestor.onReady(function (data) {
    ...
  }, {force: true, container: 'containerId', providerName: 'providerName', email:'test@test.com', debug:true});
```

Note: `force` , `container` , `providerName`, `email` , `debug` are both optional.
