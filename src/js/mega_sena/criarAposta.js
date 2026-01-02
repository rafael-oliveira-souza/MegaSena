// Note: please restart the page if syntax highlighting works bad.
let array = [[5,10,11,30,34,53],[10,21,26,34,53,55],[15,21,22,26,40,55],[5,10,21,26,53,55],[5,10,11,34,37,53],[15,21,22,26,31,55],[10,11,27,34,37,53],[5,10,34,37,46,53],[5,10,27,34,37,53],[7,21,22,26,40,55],[10,11,17,34,37,53],[7,21,22,26,31,55],[7,21,22,26,47,55],[10,11,21,26,53,55],[10,21,26,37,53,55],[10,11,27,34,46,53],[12,21,22,26,31,55],[12,21,22,26,47,55],[10,21,26,44,53,55],[10,11,21,26,47,53]];

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