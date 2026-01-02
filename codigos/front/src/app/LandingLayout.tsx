import NavBar from '../components/NavBar';
import Footer from '../components/Footer';
import logo from '../assets/Logo.png';
import BotaoWpp from '../components/BotaoWpp';

interface Props {
  children: React.ReactNode;
}

const LandingLayout = ({ children }: Props) => {
  return (
    <>
      <NavBar nomeLogo="" imgLogo={logo} barrapesquisa="Buscar..." />
      <main>{children}</main>
      <BotaoWpp/>
      <Footer />
    </>
  );
};

export default LandingLayout;
