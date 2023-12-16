// Note: please restart the page if syntax highlighting works bad.
let array = [[26,21,55,10,53,5],[15,53,6,46,8,33],[21,55,26,15,22,3],[42,25,26,38,50,44],[21,55,26,15,22,40],[21,26,55,15,22,7],[21,26,55,10,53,34],[24,9,21,41,36,27],[10,53,37,35,2,5],[55,15,21,10,53,44]];

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