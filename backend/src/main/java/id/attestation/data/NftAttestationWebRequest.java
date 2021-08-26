package id.attestation.data;

import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Introspected
public class NftAttestationWebRequest {

    @NotNull
    @NotBlank
    private String publicAttestation;

    @NotNull
    @NotEmpty
    private NftParam[] nfts;

    public String getPublicAttestation() {
        return publicAttestation;
    }

    public void setPublicAttestation(String publicAttestation) {
        this.publicAttestation = publicAttestation;
    }

    public NftParam[] getNfts() {
        return nfts;
    }

    public void setNfts(NftParam[] nfts) {
        this.nfts = nfts;
    }

    public NftAttestationWebRequest() {
    }

    public NftAttestationWebRequest(String publicAttestation, NftParam[] nfts) {
        this.publicAttestation = publicAttestation;
        this.nfts = nfts;
    }
}
