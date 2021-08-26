const attestationSite = "https://stage.attestation.id";

let Attestor = {
  onReady: function (callback, option = { force: false }) {
    let attestationFrame;

    if (option.force === undefined) {
      option.force = false;
    }

    window.addEventListener(
      "message",
      (event) => {
        console.log(event.data);

        if (event.data.ready) {
          sendMessage(attestationFrame, option);
          return;
        }

        if (event.data.display === undefined) {
          return;
        }

        attestationFrame.style.display = event.data.display ? "block" : "none";

        if (event.data.display === false) {
          callback(event.data);
          window.removeEventListener("message", null);
        }
      },
      false
    );

    attestationFrame = getOrCreateIFrame(option.container);
  },
};

function getOrCreateIFrame(container) {
  let iframe = document.getElementById("attestationFrame");
  if (!iframe) {
    iframe = document.createElement("iframe");
    iframe.setAttribute("src", attestationSite);
    iframe.setAttribute("id", "attestationFrame");
    iframe.style.display = "none";
    if (container) {
      document.getElementById(container).appendChild(iframe);
    } else {
      document.body.appendChild(iframe);
    }
  }
  return iframe;
}

function sendMessage(iframe, option) {
  let iframeWin = iframe.contentWindow;
  iframeWin.postMessage(option, attestationSite);
}
