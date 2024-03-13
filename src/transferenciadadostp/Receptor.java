package transferenciadadostp;

import java.util.Arrays;
import  transferenciadadostp.Transmissor;
import static transferenciadadostp.Transmissor.dadoBitsCRC;
import static transferenciadadostp.Transmissor.polimonio;
import static transferenciadadostp.Transmissor.printBin;
import static transferenciadadostp.Transmissor.removeZerosEsquerda;

public class Receptor {
    
    //mensagem recebida pelo transmissor
    private String mensagem;

    public Receptor() {
        //mensagem vazia no inicio da execução
        this.mensagem = "";
    }
    
    public String getMensagem() {
        return mensagem;
    }
 
    private boolean decodificarDado(boolean bits[]){
        int codigoAscii = 0;
        int expoente = bits.length-1;
        
        //converntendo os "bits" para valor inteiro para então encontrar o valor tabela ASCII
        for(int i = 0; i < bits.length;i++){
            if(bits[i]){
                codigoAscii += Math.pow(2, expoente);
            }
            expoente--;
        }
        
        //concatenando cada simbolo na mensagem original
        this.mensagem += (char)codigoAscii;
        
        //esse retorno precisa ser pensado... será que o dado sempre chega sem ruído???
        return true;
    }
    
    private static boolean decodificarDadoCRC(boolean[] bitsOriginais){
        
        //implemente a decodificação Hemming aqui e encontre os 
        //erros e faça as devidas correções para ter a imagem correta
        
        //armazena o dado orignal com os zeros adicionais em um novo vetor
        for (int bit = 0; bit < bitsOriginais.length; bit++) {
            if (bit < bitsOriginais.length)
                bitsOriginais[bit] = bitsOriginais[bit];
            else
                bitsOriginais[bit] = false;
        }
        System.out.print("Bits com Zeros: ");
        printBin(bitsOriginais);

        int posicaoInicial = 0;
        int posicaoFinal = polimonio.length - 1;

        boolean[] resto = new boolean[polimonio.length];

        //bits que serao usados na operacao XOR
        boolean[] bitsXOR = Arrays.copyOfRange(bitsOriginais, 0, polimonio.length);

        while (posicaoFinal != bitsOriginais.length) {
            int indiceResto = 0;

            System.out.print("Bits XOR:");
            printBin(bitsXOR);

            //operacao XOR com o polimonio, registrando o resultado em "resto"
            for (int indice = 0; indice < polimonio.length; indice++) {
                resto[indice] = bitsXOR[indice] != polimonio[indice];
            }

            if (posicaoFinal == bitsOriginais.length - 1)
                break;
            
            //remove os zeros a esquerda do resto e armazena a quantidade removida em zerosRemovidos
            int zerosRemovidos = removeZerosEsquerda(resto);

            //adiciona bits de volta ao resto a partir de bitsComZeros para compensar os zeros removidos
            for (int i = 0; i < zerosRemovidos; i++) {
                System.out.print("Resto: ");
                printBin(resto);
                resto[resto.length - zerosRemovidos + i] = bitsOriginais[posicaoFinal];
                posicaoFinal++;
            }
            //atualiza os bits para a proxima operacao, copiando os valores de resto para bitsXOR
            bitsXOR = Arrays.copyOf(resto, resto.length);

        }
        
        System.out.print("Resto Final: ");
        printBin(resto);
        //checa se o resto é 0
        for (boolean bit: resto) {
            //se houver algum bit 1, retorna falso
            if (bit)
                return false;
        }
        return true;
        
    }

    //recebe os dados do transmissor
    public boolean receberDadoBits(boolean bits[]){
        
        //aqui você deve trocar o médodo decofificarDado para decoficarDadoCRC (implemente!!)
        if (decodificarDadoCRC(bits)) {
            decodificarDado(bits);
            return true;
        }

        //será que sempre teremos sucesso nessa recepção
        return false;
    }
    
    //teste
    public static void main(String[] args) {
        System.out.print("Mensagem original: ");
        boolean[] mensagem = {true, false, true, true, true};
        printBin(mensagem);
        boolean[] mensagemCodificada = dadoBitsCRC(mensagem);
        System.out.print("Mensagem codificada: ");
        printBin(mensagemCodificada);
        if (decodificarDadoCRC(mensagemCodificada))
            System.out.println("mensagem correta");
        else
            System.out.println("mensagem com erro");
    }
}
