import { decrypt, encrypt, generateKey } from "../common/CryptoUtils";

test("should encrypt and decrypt correctly", async () => {
  const keys = await generateKey();
  const message = "Test Message";
  expect(
    await decrypt(keys.privateKey, await encrypt(keys.publicKey, message))
  ).toBe(message);
});
