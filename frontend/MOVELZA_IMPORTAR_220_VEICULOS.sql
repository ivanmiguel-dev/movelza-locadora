(() => {
  const estoque = window.MOVELZA_ESTOQUE;
const edicoes = JSON.parse(
    localStorage.getItem("MOVELZA_ESTOQUE_EDICOES_V1") || "{}"
  );

  const escapar = valor =>
    String(valor).replace(/'/g, "''");

  const sql = [
    "USE movelza;",
    "",
    "INSERT INTO veiculos",
    "(id, categoria, marca, modelo, cor, placa, valor_diaria, status)",
    "VALUES"
  ];

  const linhas = estoque.map(veiculo => {
    const dados = edicoes[veiculo.id] || {};
    const modelo = veiculo.modelo;

    const marca = modelo.startsWith("Mercedes-Benz")
      ? "Mercedes-Benz"
      : modelo.split(" ")[0];

    const cor = dados.cor || veiculo.cor;
    const placa = dados.placa || veiculo.placa;
    const diaria = dados.diaria ?? veiculo.diaria;
    const status = dados.status || veiculo.status;

    if (!placa || diaria == null) {
      throw new Error("Dados incompletos: " + veiculo.id);
    }

    return `('${escapar(veiculo.id)}', '${escapar(veiculo.categoria)}', '${escapar(marca)}', '${escapar(modelo)}', '${escapar(cor)}', '${escapar(placa)}', ${Number(diaria).toFixed(2)}, '${escapar(status)}')`;
  });

  sql.push(linhas.join(",\n") + ";");

  copy(sql.join("\n"));

  console.log(
    "SQL dos " + linhas.length +
    " veículos copiado para a área de transferência!"
  );
})();
