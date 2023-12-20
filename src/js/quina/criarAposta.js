// Note: please restart the page if syntax highlighting works bad.
let array = [[4,52,31,53,39],[3,48,65,25,68],[3,65,4,53,39],[3,65,4,15,39],[3,25,65,48,67],[74,38,55,11,67],[38,46,48,36,43],[3,25,4,15,66],[4,15,5,72,10],[3,53,55,58,36],[54,67,22,72,39],[17,54,75,13,48],[3,25,32,48,65],[4,15,31,66,5],[23,70,52,75,10],[7,54,50,48,55],[66,6,4,9,31],[48,1,68,3,25],[48,1,66,4,12],[48,1,66,4,33]];

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