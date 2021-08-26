<script lang="ts">
  import { onMount } from "svelte";
  import * as walletService from "../common/WalletService";
  import * as flow from "../common/Flow";
  import { current } from "../common/Flow";
  import { hasMagicLink } from "../common/MagicLink";

  import {
    STEP_CONFIRMATION,
  } from "../common/Flow";

  function connectWalletClick() {
    walletService.connect(hasMagicLink() ? STEP_CONFIRMATION :flow.transition[$current].nextStep());
  }

  onMount(async () => {
    if (await walletService.isEnabled()) {
      await walletService.connect($current);
    }
  });
</script>

<div class="title">Connect to Wallet</div>
<img
  src="assets/images/wallet/metamask.png"
  on:click={connectWalletClick}
  alt="wallet"
/>

<div class="no-wallet">
  <a href="https://metamask.io/" target="_blank">I don’t have a wallet.</a>
</div>

<style>
  img {
    width: 200px;
    height: 60px;
    margin-bottom: 16px;
    cursor: pointer;
  }

  .no-wallet {
    height: 29px;
    margin: 20px auto;
    font-size: 17px;
    font-weight: 600;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.71;
    letter-spacing: normal;
    text-align: center;
    color: #007fed;
  }
</style>
