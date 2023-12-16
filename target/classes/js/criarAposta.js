// Note: please restart the page if syntax highlighting works bad.
let array = [
  [
    26,
    21,
    55,
    10,
    53,
    5
  ],
  [
    48,
    51,
    60,
    56,
    21,
    34
  ],
  [
    3,
    8,
    41,
    43,
    47,
    28
  ],
  [
    21,
    26,
    55,
    15,
    22,
    3
  ],
  [
    21,
    26,
    55,
    15,
    7,
    22
  ],
  [
    39,
    21,
    5,
    7,
    35,
    8
  ],
  [
    55,
    15,
    21,
    10,
    53,
    37
  ],
  [
    10,
    53,
    44,
    5,
    11,
    35
  ],
  [
    21,
    15,
    55,
    47,
    22,
    26
  ],
  [
    15,
    21,
    55,
    47,
    26,
    12
  ]
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