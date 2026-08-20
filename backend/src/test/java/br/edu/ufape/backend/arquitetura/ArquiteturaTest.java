package br.edu.ufape.backend.arquitetura;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Testes automatizados de fronteiras modulares e arquitetura em camadas.
 *
 * Exceção consciente documentada:
 * O acoplamento direto entre entidades JPA de módulos distintos (ex.: AtividadeComplementar
 * referenciando a entidade Usuario) é uma concessão técnica mantida temporariamente devido ao
 * mapeamento relacional do ORM/Hibernate. O isolamento de acesso aos dados é garantido
 * estritamente impedindo o acesso cruzado aos Repositories entre módulos.
 */
@AnalyzeClasses(packages = "br.edu.ufape.backend", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquiteturaTest {

    @ArchTest
    static final ArchRule nenhumaClasseForaDeUsuarioDeveAcessarRepositoryDeUsuario =
        noClasses()
            .that().resideOutsideOfPackage("..usuario..")
            .should().dependOnClassesThat().resideInAPackage("..usuario.repository..")
            .because("O acesso a dados do módulo de usuários deve ocorrer exclusivamente via contrato (UsuarioContrato)");

    @ArchTest
    static final ArchRule nenhumaClasseForaDeAtividadeDeveAcessarRepositoryDeAtividade =
        noClasses()
            .that().resideOutsideOfPackage("..atividade..")
            .should().dependOnClassesThat().resideInAPackage("..atividade.repository..")
            .because("O repositório de atividades é de uso exclusivo interno do módulo de atividades");

    @ArchTest
    static final ArchRule controllersNaoDevemDependerDeRepositories =
        noClasses()
            .that().resideInAPackage("..controller..")
            .should().dependOnClassesThat().resideInAPackage("..repository..")
            .because("Controllers devem interagir apenas com Facades ou Services, nunca direto com a persistência");

    @ArchTest
    static final ArchRule facadesNaoDevemDependerDeRepositories =
        noClasses()
            .that().resideInAPackage("..facade..")
            .should().dependOnClassesThat().resideInAPackage("..repository..")
            .because("Facades devem orquestrar casos de uso através de Services, sem acoplamento com a persistência");
}