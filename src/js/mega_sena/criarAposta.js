// Note: please restart the page if syntax highlighting works bad.
let array = [[26,21,55,15,22,48],[21,55,26,15,22,3],[21,26,55,10,53,5],[54,58,40,1,19,47],[6,57,21,31,58,43],[10,53,34,8,5,37],[21,15,55,26,22,7],[55,15,21,10,53,37],[59,11,34,6,56,40],[15,21,55,10,53,44],[21,15,55,47,22,26],[10,53,35,11,30,44],[21,15,55,10,53,35],[2,15,31,34,4,44],[55,15,21,12,50,3],[15,55,21,12,7,50],[47,21,31,10,35,37],[31,47,15,21,51,55],[15,47,51,31,21,50],[15,21,24,38,10,35]];

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