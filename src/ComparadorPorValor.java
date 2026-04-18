import java.util.Comparator;

public class ComparadorPorValor implements Comparator<Pedido>{

	@Override
	public int compare(Pedido o1, Pedido o2) {
	
		if (o1.valorFinal() == o2.valorFinal()) {
			if (o1.getQuantosProdutos() == o2.getQuantosProdutos()) {
				return (o1.getIdPedido() - o2.getIdPedido());
			} else {
				return (o1.getQuantosProdutos() - o2.getQuantosProdutos());
			}
    	} else {
    		return (((o1.valorFinal() - o2.valorFinal()) > 0) ? 1 : -1);
        }
	}
}
