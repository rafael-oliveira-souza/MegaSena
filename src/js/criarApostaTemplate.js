// Note: please restart the page if syntax highlighting works bad.
let array = ":APOSTAS_GERADAS";

array.forEach(aposta => {
    let qtdSelecionada = parseInt(document.getElementsByClassName('input-mais-menos')[0].children[0].textContent);
    while (aposta.length !== qtdSelecionada) {
        if (aposta.length < qtdSelecionada) {
            document.getElementById('aumentarnumero').click();
        } else if (aposta.length > qtdSelecionada) {
            document.getElementById('diminuirnumero').click();
        }
        qtdSelecionada = parseInt(document.getElementsByClassName('input-mais-menos')[0].children[0].textContent);
    }

    aposta.forEach(numero => {
        let idNum = "";
        if (numero.toString().length === 1) {
            idNum = "n0" + numero;
        } else {
            idNum = "n" + numero;
        }

        document.getElementById(idNum).click();
    });
    document.getElementById("colocarnocarrinho").click();
    console.log("Aposta Feita: " + aposta);
});