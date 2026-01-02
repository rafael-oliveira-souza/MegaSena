// Note: please restart the page if syntax highlighting works bad.
let array = [[2,5,6,7,8,9,12,15,16,17,18,19,21,22,23],[1,6,7,8,10,11,13,14,16,17,20,21,23,24,25],[1,3,4,5,10,11,12,13,14,15,18,20,22,24,25],[1,3,4,5,10,11,12,13,14,15,19,20,22,24,25],[1,6,7,8,10,11,13,14,16,17,19,20,23,24,25],[2,4,5,6,7,8,9,15,16,17,18,19,21,22,23],[3,6,7,8,10,11,13,14,16,17,19,20,23,24,25],[3,6,7,8,10,11,13,14,16,17,20,21,23,24,25],[6,7,8,10,11,12,13,14,16,17,19,20,23,24,25],[1,2,4,6,7,8,9,15,16,17,18,19,21,22,23],[2,3,6,7,8,10,11,13,14,16,17,20,23,24,25],[3,6,7,8,10,11,12,13,14,16,17,19,20,23,25],[1,3,4,5,10,11,12,13,14,18,20,21,22,24,25],[1,3,4,5,10,11,12,13,14,18,19,20,22,24,25],[1,2,4,6,7,8,9,12,15,16,17,18,19,21,23],[2,4,5,6,7,8,9,12,15,16,17,18,19,21,23],[1,2,6,7,8,9,12,15,16,17,18,19,21,22,23],[1,2,4,6,7,8,9,12,15,16,17,19,21,22,23],[3,4,5,7,10,11,12,13,14,18,20,21,22,24,25],[1,3,4,7,10,11,12,13,14,18,20,21,22,24,25]];

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