import { api } from './api';

export const enviarOrcamento = async (
  form: {
    cliente: string;
    telefoneCliente: string;
    descricao: string;
    medico: string;
    crm: string;
    data: string;
  },
  receita: File
) => {
  const data = new FormData();
  data.append(
    "form",
    new Blob(
      [
        JSON.stringify({
          customerName: form.cliente,
          customerPhone: form.telefoneCliente,
          formulaDescription: form.descricao,
          doctorName: form.medico,
          crm: form.crm,
          date: form.data,
        }),
      ],
      { type: "application/json" }
    )
  );
  data.append("prescription", receita);

  return api.post("/order/submit", data, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};