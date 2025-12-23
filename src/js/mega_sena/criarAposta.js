// Note: please restart the page if syntax highlighting works bad.
let array = [[1,12,18,21,24,40],[10,12,21,29,34,46],[8,12,21,24,29,40],[7,22,35,42,49,59],[2,11,23,40,49,51],[1,9,20,42,56,60], [1,12,18,21,24,40,51],
    [5,8,16,21,25,29,46]];

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