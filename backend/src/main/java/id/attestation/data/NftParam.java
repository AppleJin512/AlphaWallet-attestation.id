package id.attestation.data;

import com.alphawallet.ethereum.ERC721Token;

import java.math.BigInteger;

public class NftParam {
    public String address;
    public BigInteger tokenId;

    public NftParam() {
    }

    public NftParam(String address, BigInteger tokenId) {
        this.address = address;
        this.tokenId = tokenId;
    }

    public ERC721Token toERC721Token(){
        return new ERC721Token(address, tokenId);
    }
}
