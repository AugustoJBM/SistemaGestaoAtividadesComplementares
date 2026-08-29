export function dataFormatada(valor: string): string {
  const partes = valor.split('T')[0].split('-');
  if (partes.length !== 3) return '';
  const [ano, mes, dia] = partes;
  return `${dia}/${mes}/${ano}`;
}
