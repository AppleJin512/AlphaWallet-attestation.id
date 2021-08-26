/* Examples:

- generateKey, return keypairs encoded as base64

generateKey().then(async (result) => {
  console.log("keys:", result);
});

- encrypt, return ciphertext

console.log(
  "encrypt result:",
  await encrypt(
    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsGCghzzvRqldzsxjmcigs/+bZ1+pvfkZS0jcG/LJmjwMRh3HTs2KVEOU9cPV3IqBgGXJa21nlO8PjEctiDofuqUwOjIhwWhK02Oey7BAxNP7pct5XUCTevVPSEk8yaV+gjSz0cox6xi/BjxNdYuYAepsKuuLjOeFQLcGLt064rYK56UEYtNlhaeGmlixOzoSkrWEYCEnVHyF9QY2MYPlEUZbKjg5frbUr8Blykmp0hszMgUhGYpEOGQWlbv5e1Qhsh88tRgkHY48I5uohPFPYTDr/XPnJJ+uWnfBVmE9y8f0zkWhiPeiDuLnWD9e1oQPOZT31nVXOoof8X4iU9yT8QIDAQAB",
    "hello"
  )
);

- decrypt, return plaintext

console.log(
  "decypt result:",
  await decrypt(
    "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCwYKCHPO9GqV3OzGOZyKCz/5tnX6m9+RlLSNwb8smaPAxGHcdOzYpUQ5T1w9XcioGAZclrbWeU7w+MRy2IOh+6pTA6MiHBaErTY57LsEDE0/uly3ldQJN69U9ISTzJpX6CNLPRyjHrGL8GPE11i5gB6mwq64uM54VAtwYu3TritgrnpQRi02WFp4aaWLE7OhKStYRgISdUfIX1BjYxg+URRlsqODl+ttSvwGXKSanSGzMyBSEZikQ4ZBaVu/l7VCGyHzy1GCQdjjwjm6iE8U9hMOv9c+ckn65ad8FWYT3Lx/TORaGI96IO4udYP17WhA85lPfWdVc6ih/xfiJT3JPxAgMBAAECggEALdJMohXAi/kAN/N9hLxMk7a6JtcBgCsw2RAqrtAadAlZnvWpi912LIIKNvcTnmIVVsga1d/sYxLgVS9Asv12tMSpjNTtVBPGLLjazkwR55Cy9dWwx/s41V7ZpAvy0Og/v4/1j3EaGtuo9PPBo+IKMaU3SMc8z/Rt9+wUnSQ4YiE+Lw/DMT9FlDoPWRLQVV26HUrTlb4c8r/qohw5hMXmv89s/gfyNK/yXvbNYo9PrzHIq4bHWOD+wgJuJeV0iTIgf1vGPzfE1m4zQSyt3sAVTu8bDHkXpi0cRFkTZDvCAiiHklgzxQ1NBdyoapRGmr2zsDzThMPWhNLKR5+1s8N9yQKBgQDqaBpfVxOVs5mL/857qsL9VZ26VGGnsJ/30bsfrIP7U9ODSSv4mX4XDVXOutjwcPQfR4q11hziMDQoDZmxQ47XhKSabBTGZyIKLwNz8/eJMMF4tPT8cbX/QoSDdhadXjqMFH5xl1hUj1/HIQCBZcTAStimmzF/goWzf9+klY0mnwKBgQDAoAzCzTkrJ9q+Q0yxCSwAeU/zLOQ8W1iFFf0eJInb4Z9G8R9BVkI6PNjEcrJG1Db83IDPpdELvcWn+pABz3EvF0CC59JCGpNiHAyv7ccK24lut/giQJGoVehcFBZy3uJlSM1hXN8sffQKbOILy63rowma6cfOzsMH/462tQALbwKBgQC87rGq/M1dUQhGziVIiidl95cM8yxO887L+TDg47TxYuxNmjGly1nVDLuHyBRJIFGz0H8d9vkK2p1/Vw700KYWUeQ2Tz0jeUb3fFNFJ3PyEOkv+HKp8qEloCCcsRajOqrn8zDDL6BTb8hcAN6ebT97WhhrmRT5WGOZfUzuJFhDswKBgAjh2HTLHc29orqPQ+QN9jd3YJZoBYY6+BP69ZcEPE8lUkDlMXnDzn38/EniuBHIC1kRmeb5UHBoKcsbJLTQqflv6wueQPXHX/BwNq2OG1WG1gmC9jAuJglLHNHSI07ctDfTaZUJwUi97hjk+G9uzvBErla0XQBOTHP79sq6AeyFAoGBAOYkGkrsei/rcf1HSt7yjxJaBOD9N0FQr9flJQPs8Undo27V1hIUpxFXiOPS7Onu18BHyNLuhCT8l3Ow+2T5q+8WSy9mpxxTKDx016RjsEQ0YeXarWP2tGjEdVbGUM1sOl7zSdeRUGYqQypL3FVQW36qGIlWyG/YzWZmCOW4RWce",
    "IxryOWLHVbDLroZsZFiOnWNkizj/KSyfIL8SvviJe73hDAWIQtmeDVxs3VbhbtdmWEWugTmxupvaPNlO/AXhuLf3Qv4y90qnTMruOln+KxgEVqWYFjsDXo0gba3Tjf+FCGNVZ0d0MViXUpkDXQFfv3E1+vayKSC8AiK8zfx2Ky3EwKKU44K4yZ1po8vAdud1ZwRfjLpIUDT88UJCoSH5tJuPPeaMEznUyAaMbRMgRBkeq+HZnypY04dRNTpBoZcTHEbZmcRIYSYu45jKv03YgRtEDoZ0493SdjmrbYKg0jp8DyS0zchuLS7wM7S/AzaJH/8Mxk+YF+SrwE7FD9z7Lw=="
  )
);

 */

import { ab2str, str2ab } from "./Utils";

async function importKey(key) {
  return await window.crypto.subtle.importKey(
    "pkcs8",
    str2ab(atob(key)),
    {
      name: "RSA-OAEP",
      hash: "SHA-256",
      modulusLength: 2048,
      publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
    },
    true,
    ["decrypt"]
  );
}

async function importPKey(key) {
  return await window.crypto.subtle.importKey(
    "spki",
    str2ab(atob(key)),
    {
      name: "RSA-OAEP",
      hash: "SHA-256",
      modulusLength: 2048,
      publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
    },
    true,
    ["encrypt"]
  );
}

declare let window: any;

export async function generateKey() {
  const key = await window.crypto.subtle.generateKey(
    {
      name: "RSA-OAEP",
      modulusLength: 2048, //can be 1024, 2048, or 4096
      publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
      hash: { name: "SHA-256" }, //can be "SHA-1", "SHA-256", "SHA-384", or "SHA-512"
    },
    true, //whether the key is extractable (i.e. can be used in exportKey)
    ["encrypt", "decrypt"] //must be ["encrypt", "decrypt"] or ["wrapKey", "unwrapKey"]
  );

  return {
    publicKey: btoa(
      ab2str(await window.crypto.subtle.exportKey("spki", key.publicKey))
    ),
    privateKey: btoa(
      ab2str(await window.crypto.subtle.exportKey("pkcs8", key.privateKey))
    ),
  };
}

export async function decrypt(privateKey: string, data: string) {
  const key = await importKey(privateKey);

  return ab2str(
    await window.crypto.subtle.decrypt(
      {
        name: "RSA-OAEP",
        hash: "SHA-256",
        modulusLength: 2048,
        publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
      },
      key,
      str2ab(atob(data))
    )
  );
}

export async function encrypt(publicKey: string, data: string) {
  const key = await importPKey(publicKey);

  return btoa(
    ab2str(
      await window.crypto.subtle.encrypt(
        {
          name: "RSA-OAEP",
          hash: "SHA-256",
          modulusLength: 2048,
          publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
        },
        key,
        str2ab(data)
      )
    )
  );
}
