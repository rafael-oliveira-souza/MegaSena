// Note: please restart the page if syntax highlighting works bad.
let array = [[20,10,11,25,13,24,14,3,5,4,9,1,12,18,22],[26,27,28,29,30,31,32,20,10,25,11,13,24,14,3],[20,10,11,25,24,13,14,3,9,4,5,18,12,22,21],[50,3,46,18,51,52,60,28,15,4,39,49,25,57,16],[20,10,11,25,24,14,3,13,18,5,9,22,4,7,21],[26,27,28,29,30,31,32,20,10,11,24,25,14,13,18],[26,27,28,29,30,31,32,33,34,35,36,37,38,39,40],[20,10,25,24,14,11,3,13,18,5,4,9,22,7,12],[26,27,28,29,30,31,32,20,10,25,11,14,18,5,24],[26,27,28,29,30,31,32,20,10,25,5,11,14,18,3]];

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