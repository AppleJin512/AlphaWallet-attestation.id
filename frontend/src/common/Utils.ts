export function ab2str(buf) {
  return String.fromCharCode.apply(null, new Uint8Array(buf));
}

export function str2ab(str) {
  const buf = new ArrayBuffer(str.length);
  const bufView = new Uint8Array(buf);
  for (let i = 0, strLen = str.length; i < strLen; i++) {
    bufView[i] = str.charCodeAt(i);
  }
  return buf;
}

export function uint8merge(list: Uint8Array[]): Uint8Array {
  if (list.length === 1) return list[0];

  let out = Uint8Array.from([]);
  if (list.length === 0) return out;

  for (let i = 0; i < list.length; i++) {
    let temp = new Uint8Array(out.length + list[i].length);
    temp.set(out);
    temp.set(list[i], out.length);
    out = temp;
  }
  return out;
}

export function BnPowMod(base: bigint, n: bigint, mod: bigint) {
  let res = 1n,
    cur = base;
  while (n > 0n) {
    if (n & 1n) res = (res * cur) % mod;
    cur = (cur * cur) % mod;
    n >>= 1n;
  }
  return res;
}

export function base64toBase64Url(base64: string): string {
  return base64.replace(/\+/g, "-").replace(/\//g, "_").replace(/\=+$/, "");
  // return base64.split("/").join("_").split("+").join("-").split("=").join(".");
}
