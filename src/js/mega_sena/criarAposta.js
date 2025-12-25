// Note: please restart the page if syntax highlighting works bad.
let array = [[1, 17, 21, 22, 26, 39, 41, 43, 45, 53, 55]];

array.forEach(aposta => {
    var quantidadeNumeros = 6;
    while (aposta.length !== quantidadeNumeros) {
        if (aposta.length > quantidadeNumeros) {
            document.getElementById('aumentarnumero').click();
            quantidadeNumeros++;
        } else if (aposta.length < quantidadeNumeros) {
            document.getElementById('diminuirnumero').click();
            quantidadeNumeros--;
        }
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