import '../styles/ListaDeProdutos.css';

interface ListaDeProdutosProps {
  produtos: string[]; // Agora é apenas uma lista de strings representando as categorias
  onSelecionar: (categoria: string) => void;
  filtroAtual: string | null;
}

const ListaDeProdutos: React.FC<ListaDeProdutosProps> = ({
  produtos,
  onSelecionar,
  filtroAtual,
}) => {
  return (
    <section className="lista-produtos">
      {produtos.map((categoria) => (
        <span
          className={`produto-item ${filtroAtual === categoria ? 'ativo' : ''}`}
          key={categoria}
          onClick={() => onSelecionar(categoria)}
        >
          {categoria}
        </span>
      ))}
    </section>
  );
};

export default ListaDeProdutos;