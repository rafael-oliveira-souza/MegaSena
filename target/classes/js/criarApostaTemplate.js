// Note: please restart the page if syntax highlighting works bad.
let array = ":APOSTAS_GERADAS";

array.forEach(aposta => {
    // setInterval(function () {
        aposta.forEach(numero => {
            let idNum = "";
            if(numero.toString().length == 1){
                idNum = "n0" + numero;
            }else{
                idNum = "n" + numero;
            }

            document.getElementById(idNum).click();
        });
        document.getElementById("colocarnocarrinho").click();
        console.log("Aposta Feita: " + aposta);
    // }, 10000);
})