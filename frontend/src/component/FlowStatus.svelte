<script lang="ts">
  import {
    current,
    STEP_CONFIRMATION,
    STEP_CONNECT_WALLET,
    STEP_ENTER_EMAIL,
    STEP_FINAL,
  } from "../common/Flow";

  const steps = [
    {
      id: STEP_ENTER_EMAIL,
      text: "Email",
      verified: false,
      active: false,
    },
    {
      id: STEP_CONFIRMATION,
      text: "Confirm",
      verified: false,
      active: false,
    },
    {
      id: STEP_CONNECT_WALLET,
      text: "Connect",
      verified: false,
      active: false,
    },
    {
      id: STEP_FINAL,
      text: "Finish",
      verified: false,
      active: false,
    },
  ];

  const refreshProcessList = function (step) {
    const index = steps.findIndex((item) => item.id === step);
    if (index > -1) {
      for (let i = 0; i < steps.length; i++) {
        if (i < index) {
          steps[i].verified = true;
        } else {
          steps[i].verified = false;
        }
        steps[i].active = false;
      }
      steps[index].active = true;
    }

    return steps;
  };

  $: refreshProcessList($current);
</script>

<div class="steps">
  {#each steps as step}
    <div class="step {step.active ? 'active' : ''} short">
      {#if step.verified}
        <img alt="verified" src="assets/images/verified.png" class="verified" />
      {/if}
      <span>{step.text}</span>
    </div>
  {/each}
</div>

<style>
  .step {
    display: inline-block;
    height: 40px;
    width: 33%;
    font-size: 15px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.67;
    letter-spacing: normal;
    text-align: center;
    color: #727272;
  }

  .step span {
    line-height: 40px;
  }

  .active {
    font-size: 15px;
    font-weight: 600;
    line-height: 1.67;
    color: #000000;
    background-image: url("/assets/images/path.png");
    background-repeat: no-repeat;
    background-size: contain;
    background-position: center center;
  }

  .verified {
    width: 16px;
    height: 16px;
    vertical-align: middle;
  }

  .steps {
    border-bottom: 1px solid #dddddd;
    padding-bottom: 11px;
  }

  .short {
    width: 25%;
  }
</style>
