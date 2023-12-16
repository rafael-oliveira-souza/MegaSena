// Note: please restart the page if syntax highlighting works bad.
let array = [
    [21,10,56,38,34,53],
    [10,1,41,37,58,47],
    [37,8,40,50,51,14],
    [7,37,48,46,45,52],
    [30,19,25,33,18,13],
    [40,15,37,48,7,43],
    [51,26,40,47,53,16],
    [25,2,9,49,32,11],
    [19,29,34,14,13,49],
    [1,18,8,30,43,4],
    [44,55,36,3,31,59],
    [7,17,18,25,14,21],
    [12,14,41,26,47,7],
    [55,43,57,16,3,19],
    [37,8,32,30,57,31],
    [20,33,13,46,3,48],
    [14,16,22,20,51,3],
    [40,44,47,42,48,11],
    [60,15,37,34,38,14],
    [4,56,46,6,2,10]
];

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