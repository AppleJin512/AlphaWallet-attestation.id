import { AttestationDb } from "../common/AttestationDb";

const attestationDb = new AttestationDb();
attestationDb.initDb();

test("insertAttestation should work", async () => {
  const result = await attestationDb.insertAttestation(
    "email",
    "jason@smarttokenlabs.com",
    "0xa12A1AfFe1623b0976ECB06360C0ce1C0263C7de",
    {
      attestation:
        "MIICUjCCAf+gAwIBEgIIBkzB67zGqq4wCQYHKoZIzj0EAjAWMRQwEgYDVQQDDAtBbHBoYVdhbGxldDAuGA8yMDIyMDcwODE1NDUwN1oCBGLIUQMYDzIwMjIwODA3MTU0NTA3WgIEYu/eAzALMQkwBwYDVQQDDAAwggEzMIHsBgcqhkjOPQIBMIHgAgEBMCwGByqGSM49AQECIQD////////////////////////////////////+///8LzBEBCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAcEQQR5vmZ++dy7rFWgYpXOhwsHApv82y3OKNlZ8oFbFvgXmEg62ncmo8RlXaT7/A4RCKj9F7RIpoVUGZxH0I/7ENS4AiEA/////////////////////rqu3OavSKA7v9JejNA2QUECAQEDQgAES7NPtij67VaedqdMDxJTl9rWPI3uUWvjQYwego5CmDBqyH9wYeNCdkcaevNshwVdRx3JD5pV9t7f7aXIFRVf66NXMFUwUwYLKwYBBAGLOnN5ASgBAf8EQQQYkQtwylzF0dUqm6hVZnCrom/DS+QFYDucqlxzhx1EHSW2v/49xygWjfMZtXJn7uENGBW5n8HH5f31hwJSzddmMAkGByqGSM49BAIDQgDsQgVZlMmmZ1saytS5r6u/RjYXX71jkmtZDO9b+j6JlELsWrNFMMRjj/L6NlloB/74UXMGdvP5gzNJi36XOboNHA==",
      requestSecret:
        "c5f5ef0a8a2a7543810efc3888d17f452379aee0c95a2b574aa38aa34e30db6",
    }
  );
  expect(result).toBe(1);
});

test("getFirstAttestationByEmail should work", async () => {
  const result = await attestationDb.getFirstAttestationByEmail(
    "email",
    "jason@smarttokenlabs.com"
  );
  expect(result).toStrictEqual({
    attestation:
      "MIICUjCCAf+gAwIBEgIIBkzB67zGqq4wCQYHKoZIzj0EAjAWMRQwEgYDVQQDDAtBbHBoYVdhbGxldDAuGA8yMDIyMDcwODE1NDUwN1oCBGLIUQMYDzIwMjIwODA3MTU0NTA3WgIEYu/eAzALMQkwBwYDVQQDDAAwggEzMIHsBgcqhkjOPQIBMIHgAgEBMCwGByqGSM49AQECIQD////////////////////////////////////+///8LzBEBCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAcEQQR5vmZ++dy7rFWgYpXOhwsHApv82y3OKNlZ8oFbFvgXmEg62ncmo8RlXaT7/A4RCKj9F7RIpoVUGZxH0I/7ENS4AiEA/////////////////////rqu3OavSKA7v9JejNA2QUECAQEDQgAES7NPtij67VaedqdMDxJTl9rWPI3uUWvjQYwego5CmDBqyH9wYeNCdkcaevNshwVdRx3JD5pV9t7f7aXIFRVf66NXMFUwUwYLKwYBBAGLOnN5ASgBAf8EQQQYkQtwylzF0dUqm6hVZnCrom/DS+QFYDucqlxzhx1EHSW2v/49xygWjfMZtXJn7uENGBW5n8HH5f31hwJSzddmMAkGByqGSM49BAIDQgDsQgVZlMmmZ1saytS5r6u/RjYXX71jkmtZDO9b+j6JlELsWrNFMMRjj/L6NlloB/74UXMGdvP5gzNJi36XOboNHA==",
    requestSecret:
      "c5f5ef0a8a2a7543810efc3888d17f452379aee0c95a2b574aa38aa34e30db6",
  });
});

test("getAttestation should work", async () => {
  const result = await attestationDb.getAttestation(
    "email",
    "jason@smarttokenlabs.com",
    "0xa12A1AfFe1623b0976ECB06360C0ce1C0263C7de"
  );
  expect(result).toStrictEqual({
    attestation:
      "MIICUjCCAf+gAwIBEgIIBkzB67zGqq4wCQYHKoZIzj0EAjAWMRQwEgYDVQQDDAtBbHBoYVdhbGxldDAuGA8yMDIyMDcwODE1NDUwN1oCBGLIUQMYDzIwMjIwODA3MTU0NTA3WgIEYu/eAzALMQkwBwYDVQQDDAAwggEzMIHsBgcqhkjOPQIBMIHgAgEBMCwGByqGSM49AQECIQD////////////////////////////////////+///8LzBEBCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAcEQQR5vmZ++dy7rFWgYpXOhwsHApv82y3OKNlZ8oFbFvgXmEg62ncmo8RlXaT7/A4RCKj9F7RIpoVUGZxH0I/7ENS4AiEA/////////////////////rqu3OavSKA7v9JejNA2QUECAQEDQgAES7NPtij67VaedqdMDxJTl9rWPI3uUWvjQYwego5CmDBqyH9wYeNCdkcaevNshwVdRx3JD5pV9t7f7aXIFRVf66NXMFUwUwYLKwYBBAGLOnN5ASgBAf8EQQQYkQtwylzF0dUqm6hVZnCrom/DS+QFYDucqlxzhx1EHSW2v/49xygWjfMZtXJn7uENGBW5n8HH5f31hwJSzddmMAkGByqGSM49BAIDQgDsQgVZlMmmZ1saytS5r6u/RjYXX71jkmtZDO9b+j6JlELsWrNFMMRjj/L6NlloB/74UXMGdvP5gzNJi36XOboNHA==",
    requestSecret:
      "c5f5ef0a8a2a7543810efc3888d17f452379aee0c95a2b574aa38aa34e30db6",
  });
});
