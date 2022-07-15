<!-- routify:options preload="proximity" -->
<script>
    import FlowStatus from "../component/FlowStatus.svelte";
    import Header from "../component/Header.svelte";
    import Footer from "../component/Footer.svelte";
</script>
<Header />
<FlowStatus />
<main>
    <slot />
</main>
<Footer />