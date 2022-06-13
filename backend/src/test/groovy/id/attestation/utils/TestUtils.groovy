package id.attestation.utils


import id.attestation.data.NftParam

import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec

class TestUtils {

    static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsGCghzzvRqldzsxjmcigs/+bZ1+pvfkZS0jcG/LJmjwMRh3HTs2KVEOU9cPV3IqBgGXJa21nlO8PjEctiDofuqUwOjIhwWhK02Oey7BAxNP7pct5XUCTevVPSEk8yaV+gjSz0cox6xi/BjxNdYuYAepsKuuLjOeFQLcGLt064rYK56UEYtNlhaeGmlixOzoSkrWEYCEnVHyF9QY2MYPlEUZbKjg5frbUr8Blykmp0hszMgUhGYpEOGQWlbv5e1Qhsh88tRgkHY48I5uohPFPYTDr/XPnJJ+uWnfBVmE9y8f0zkWhiPeiDuLnWD9e1oQPOZT31nVXOoof8X4iU9yT8QIDAQAB"
    static String invalidPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsGCghzzvRqldzsxjmcigsbZ1+pvfkZS0jcG/LJmjwMRh3HTs2KVEOU9cPV3IqBgGXJa21nlO8PjEctiDofuqUwOjIhwWhK02Oey7BAxNP7pct5XUCTevVPSEk8yaV+gjSz0cox6xi/BjxNdYuYAepsKuuLjOeFQLcGLt064rYK56UEYtNlhaeGmlixOzoSkrWEYCEnVHyF9QY2MYPlEUZbKjg5frbUr8Blykmp0hszMgUhGYpEOGQWlbv5e1Qhsh88tRgkHY48I5uohPFPYTDr/XPnJJ+uWnfBVmE9y8f0zkWhiPeiDuLnWD9e1oQPOZT31nVXOoof8X4iU9yT8QIDAQAB"
    static String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCwYKCHPO9GqV3OzGOZyKCz/5tnX6m9+RlLSNwb8smaPAxGHcdOzYpUQ5T1w9XcioGAZclrbWeU7w+MRy2IOh+6pTA6MiHBaErTY57LsEDE0/uly3ldQJN69U9ISTzJpX6CNLPRyjHrGL8GPE11i5gB6mwq64uM54VAtwYu3TritgrnpQRi02WFp4aaWLE7OhKStYRgISdUfIX1BjYxg+URRlsqODl+ttSvwGXKSanSGzMyBSEZikQ4ZBaVu/l7VCGyHzy1GCQdjjwjm6iE8U9hMOv9c+ckn65ad8FWYT3Lx/TORaGI96IO4udYP17WhA85lPfWdVc6ih/xfiJT3JPxAgMBAAECggEALdJMohXAi/kAN/N9hLxMk7a6JtcBgCsw2RAqrtAadAlZnvWpi912LIIKNvcTnmIVVsga1d/sYxLgVS9Asv12tMSpjNTtVBPGLLjazkwR55Cy9dWwx/s41V7ZpAvy0Og/v4/1j3EaGtuo9PPBo+IKMaU3SMc8z/Rt9+wUnSQ4YiE+Lw/DMT9FlDoPWRLQVV26HUrTlb4c8r/qohw5hMXmv89s/gfyNK/yXvbNYo9PrzHIq4bHWOD+wgJuJeV0iTIgf1vGPzfE1m4zQSyt3sAVTu8bDHkXpi0cRFkTZDvCAiiHklgzxQ1NBdyoapRGmr2zsDzThMPWhNLKR5+1s8N9yQKBgQDqaBpfVxOVs5mL/857qsL9VZ26VGGnsJ/30bsfrIP7U9ODSSv4mX4XDVXOutjwcPQfR4q11hziMDQoDZmxQ47XhKSabBTGZyIKLwNz8/eJMMF4tPT8cbX/QoSDdhadXjqMFH5xl1hUj1/HIQCBZcTAStimmzF/goWzf9+klY0mnwKBgQDAoAzCzTkrJ9q+Q0yxCSwAeU/zLOQ8W1iFFf0eJInb4Z9G8R9BVkI6PNjEcrJG1Db83IDPpdELvcWn+pABz3EvF0CC59JCGpNiHAyv7ccK24lut/giQJGoVehcFBZy3uJlSM1hXN8sffQKbOILy63rowma6cfOzsMH/462tQALbwKBgQC87rGq/M1dUQhGziVIiidl95cM8yxO887L+TDg47TxYuxNmjGly1nVDLuHyBRJIFGz0H8d9vkK2p1/Vw700KYWUeQ2Tz0jeUb3fFNFJ3PyEOkv+HKp8qEloCCcsRajOqrn8zDDL6BTb8hcAN6ebT97WhhrmRT5WGOZfUzuJFhDswKBgAjh2HTLHc29orqPQ+QN9jd3YJZoBYY6+BP69ZcEPE8lUkDlMXnDzn38/EniuBHIC1kRmeb5UHBoKcsbJLTQqflv6wueQPXHX/BwNq2OG1WG1gmC9jAuJglLHNHSI07ctDfTaZUJwUi97hjk+G9uzvBErla0XQBOTHP79sq6AeyFAoGBAOYkGkrsei/rcf1HSt7yjxJaBOD9N0FQr9flJQPs8Undo27V1hIUpxFXiOPS7Onu18BHyNLuhCT8l3Ow+2T5q+8WSy9mpxxTKDx016RjsEQ0YeXarWP2tGjEdVbGUM1sOl7zSdeRUGYqQypL3FVQW36qGIlWyG/YzWZmCOW4RWce"
    static String publicRequest = "{\"signatureInHex\":\"0xaf25ca3238aff0bb87d4bb45c540dc3217c3e21daec1516e9d7f10c98b76615716d57449ad49e0a9896ed6108248e78ad4bfa194b52c625e36def0033001243d1c\",\"jsonSigned\":\"{\\\"types\\\":{\\\"EIP712Domain\\\":[{\\\"name\\\":\\\"name\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"version\\\",\\\"type\\\":\\\"string\\\"}],\\\"AttestationRequest\\\":[{\\\"name\\\":\\\"payload\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"description\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"timestamp\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"identifier\\\",\\\"type\\\":\\\"string\\\"}]},\\\"primaryType\\\":\\\"AttestationRequest\\\",\\\"message\\\":{\\\"payload\\\":\\\"MIIBAgIBATCB_ARBBBjwEjzQbGrtOsseGCN5GpHHq6lohY4zJLCpwfuE6bODF_8u8_61Oe3b--sqEw8KY3UDF3NX2UnsJ33-XGiLo80EICo_Vkjt-Y8sO_F-9Ym1kYR452HLiPOpY2qWYbpVjh8RBEEEFcyHNLEE0p5iKOA_v7MRU7rZ6H6g_Pt-rJpSCWx8EskQG3hB2vLzg4YbujGUjm2mZROwwPcCkirKcX3eNnmFggRSMFgxNkYxQ0RGNTIwMEQ3QUU3RjA3QzE1MjJGMTkwNTJBNzIyRDkzOTcwqZU50y9kqXbiNUmNpswjqJjyXO1uD4eQiw8qoNwfgHwAAAF5RuJFWA\\\",\\\"description\\\":\\\"Linking Ethereum address to phone or email\\\",\\\"timestamp\\\":\\\"Fri May 07 2021 20:51:44 GMT+0800\\\",\\\"identifier\\\":\\\"jianhgreat@hotmail.com\\\"},\\\"domain\\\":{\\\"name\\\":\\\"http://wwww.attestation.id\\\",\\\"version\\\":\\\"0.1\\\"}}\"}"
    static String badPublicRequest = "MIICSjCBxhoWamlhbmhncmVhdEBob3RtYWlsLmNvbQIBATCBqARBBAb3ILnJYpovUWDsm4cbgODL+AM4DItGmsiRUMOUjAkFG8HTMTjcNwOoTijWgb8CQSXtg7LCHU+pTzm1lJKg4ZMEICoGyTUzXpTzN+OfhPu4xuwZObc7elsZovLlkF/6MoxmBEEEJvqdfO+lprkpDT5h0QSU2Wq3D+C/jikciAyIwn8RhCUkRMpSI+nbEwzjBo4/cQTXz9FOnFxkAV3T4JfolWiUdjCCATMwgewGByqGSM49AgEwgeACAQEwLAYHKoZIzj0BAQIhAP////////////////////////////////////7///wvMEQEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABwRBBHm+Zn753LusVaBilc6HCwcCm/zbLc4o2VnygVsW+BeYSDradyajxGVdpPv8DhEIqP0XtEimhVQZnEfQj/sQ1LgCIQD////////////////////+uq7c5q9IoDu/0l6M0DZBQQIBAQNCAAT+UQLhGS0DPClXCSm5O5K6K3AhAuiw2QMcIUB9cpYz+/7/lAwEYHkehJzxcJ34quLPZTTdQKbs1NdEIWK812NOA0gAMEUCIE1+WoamO7E4+ulMumxH1EO0kdA5vnJA6tfElgM00NqBAiEAjR9A82xXcgozuMse9H0tA3iT96M038WePcX0VzIp1lI="

    static String ethAddress = "0x16f1CdF5200d7ae7f07c1522f19052A722D93970"
    static String message = "536f6d65207465787420666f72204175746f6772617068657220746f207369676e20736f2077652063616e2067656e657261746520746865206174746573746174696f6e"
    static String signature = "0xa31ab92f335062cee755257a8157fbef20d98abeb06ec3fd6975b2ab3018c9327daaf2d91fc4efdffcd10c2ddcca37f37ffecf16fb3910f6881b39bb99e1fcf01c"
    static String twitterIdentifier = "https://twitter.com/foxgem"
    static String mock1Identifier = "http://mock1.example.com/abcfy2"
    static String mock2Identifier = "https://mock2.example.com/abcfy2"

    static String publicAttestation = "MIICEjCCAb+gAwIBEwIBATAJBgcqhkjOPQQCMBkxFzAVBgNVBAMMDmF0dGVzdGF0aW9uLmlkMCIYDzIwMjEwODI2MTI0ODU5WhgPOTk5OTEyMzExNTU5NTlaMDQxMjAwBgkrBgEEAYF6ATkMI2h0dHBzOi8vdHdpdHRlci5jb20vZm94Z2VtIDE0MTkwNDg2MIIBMzCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////////////////////////////////////v///C8wRAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHBEEEeb5mfvncu6xVoGKVzocLBwKb/NstzijZWfKBWxb4F5hIOtp3JqPEZV2k+/wOEQio/Re0SKaFVBmcR9CP+xDUuAIhAP////////////////////66rtzmr0igO7/SXozQNkFBAgEBA0IABDNehahIPcRJHPe/te5LNC8ycKIGte/3rKC8WBv1eqkYJ3PoXls5ts4wK88VT3nSjZmqQwboqZ71ULFONTSJ5okwCQYHKoZIzj0EAgNCAHIYNnna8rY3jOKMsvCy6fYXiU9TAWjKqgkJNVbQQEHcU0WHcw+C0pwpQeR9rCbMxaDHi8z6x6K4Th4EaI7VrOwc"
    static def nfts1 = new NftParam[]{new NftParam("0xa567f5a165545fa2639bbda79991f105eadf8522", new BigInteger("1"))}
    static def nfts2 = new NftParam[]{
            new NftParam("0xa567f5a165545fa2639bbda79991f105eadf8522", new BigInteger("1"))
            , new NftParam("0xa567f5a165545fa2639bbda79991f105eadf8522", new BigInteger("2"))
    }

    static String subjectPublicKey = "MIIBMzCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////////////////////////////////////v///C8wRAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHBEEEeb5mfvncu6xVoGKVzocLBwKb/NstzijZWfKBWxb4F5hIOtp3JqPEZV2k+/wOEQio/Re0SKaFVBmcR9CP+xDUuAIhAP////////////////////66rtzmr0igO7/SXozQNkFBAgEBA0IABDNehahIPcRJHPe/te5LNC8ycKIGte/3rKC8WBv1eqkYJ3PoXls5ts4wK88VT3nSjZmqQwboqZ71ULFONTSJ5ok="
    static String nftAttestation = "0x3082023430820212308201bfa003020113020101300906072a8648ce3d040230193117301506035504030c0e6174746573746174696f6e2e69643022180f32303231303832363132343835395a180f39393939313233313135353935395a30343132303006092b06010401817a01390c2368747470733a2f2f747769747465722e636f6d2f666f7867656d203134313930343836308201333081ec06072a8648ce3d02013081e0020101302c06072a8648ce3d0101022100fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f3044042000000000000000000000000000000000000000000000000000000000000000000420000000000000000000000000000000000000000000000000000000000000000704410479be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8022100fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd036414102010103420004335e85a8483dc4491cf7bfb5ee4b342f3270a206b5eff7aca0bc581bf57aa9182773e85e5b39b6ce302bcf154f79d28d99aa4306e8a99ef550b14e353489e689300906072a8648ce3d040203420072183679daf2b6378ce28cb2f0b2e9f617894f530168caaa09093556d04041dc534587730f82d29c2941e47dac26ccc5a0c78bccfac7a2b84e1e04688ed5acec1c301c301a04140000853abe6fa93f276b62f6984b75ff2f9dc30e040200b7"
    static String nftAttestationSignature = "0x5de502d20d72e60695a10561a56f1210f90e07029c38598092e82867719a6bbb1cbe0aca3e4b0a4b7ae5cf35c2342851eb1a63fceb0e7877cc170abde011ad4a1b"

    static String paForCosigned = "MIICFDCCAcGgAwIBEwIBATAJBgcqhkjOPQQCMBkxFzAVBgNVBAMMDmF0dGVzdGF0aW9uLmlkMCIYDzIwMjExMDEzMDkzNjM3WhgPOTk5OTEyMzExNTU5NTlaMDYxNDAyBgkrBgEEAYF6ATkMJWh0dHBzOi8vdHdpdHRlci5jb20vMTQxOTA0ODYgMTQxOTA0ODYwggEzMIHsBgcqhkjOPQIBMIHgAgEBMCwGByqGSM49AQECIQD////////////////////////////////////+///8LzBEBCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAcEQQR5vmZ++dy7rFWgYpXOhwsHApv82y3OKNlZ8oFbFvgXmEg62ncmo8RlXaT7/A4RCKj9F7RIpoVUGZxH0I/7ENS4AiEA/////////////////////rqu3OavSKA7v9JejNA2QUECAQEDQgAEM16FqEg9xEkc97+17ks0LzJwoga17/esoLxYG/V6qRgnc+heWzm2zjArzxVPedKNmapDBuipnvVQsU41NInmiTAJBgcqhkjOPQQCA0IAc95//eeRvNQT1zoKHYb2+fJ/iBCvmlKKqyMhbbb5WKIxUUG2VsqhAoRQJ9I1slEwXMYdqQM26TbkksImKa5uoRw="
    static String pkForCosigned =  "MIIBMzCB7AYHKoZIzj0CATCB4AIBATAsBgcqhkjOPQEBAiEA/////////////////////////////////////v///C8wRAQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHBEEEeb5mfvncu6xVoGKVzocLBwKb/NstzijZWfKBWxb4F5hIOtp3JqPEZV2k+/wOEQio/Re0SKaFVBmcR9CP+xDUuAIhAP////////////////////66rtzmr0igO7/SXozQNkFBAgEBA0IABDNehahIPcRJHPe/te5LNC8ycKIGte/3rKC8WBv1eqkYJ3PoXls5ts4wK88VT3nSjZmqQwboqZ71ULFONTSJ5ok="
    static String signatureForCosigned = "0xc2e4c07b65cc59971297be38da3f7a409bdbb38e293eb23aa0d6827c27839acf7bd46f01f50e5fe46f0a878782a6809c86654eb5deabd828c656004d3b6bc38c1c"

    static String ecPubkeyHex = "950C7C0BED23C3CAC5CC31BBB9AAD9BB5532387882670AC2B1CDF0799AB0EBC764C267F704E8FDDA0796AB8397A4D2101024D24C4EFFF695B3A417F2ED0E48CD"

    static PrivateKey getPrivateKey(String privateKey) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decoder.decode(privateKey))
        KeyFactory keyFactory = KeyFactory.getInstance("RSA")
        PrivateKey result = keyFactory.generatePrivate(keySpec)
        return result
    }

    static String decryptWithRSAOAEP(String privateKey, String data) {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1"
                , new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT)
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey), oaepParams)
        return new String(cipher.doFinal(Base64.decoder.decode(data)))
    }
}
