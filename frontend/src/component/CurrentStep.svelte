<script>
  import ConnectWallet from "./ConnectWallet.svelte";
  import EnterEmail from "./EnterEmail.svelte";
  import Confirmation from "./Confirmation.svelte";
  import Final from "./Final.svelte";
  import {
    current,
    STEP_CONNECT_WALLET,
    STEP_ENTER_EMAIL,
    STEP_CONFIRMATION,
    STEP_FINAL,
  } from "../common/Flow";
</script>

{#if $current === STEP_CONNECT_WALLET}
  <ConnectWallet />
{:else if $current === STEP_ENTER_EMAIL}
  <EnterEmail />
{:else if $current === STEP_CONFIRMATION}
  <Confirmation />
{:else if $current === STEP_FINAL}
  <Final />
{/if}
