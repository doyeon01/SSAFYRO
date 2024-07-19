export default function Navbar() {
    return (
        <>
            <nav className="flex py-2 px-2">
                {/* 로고 자리 */}
                <div className="flex-auto text-start">
                    <a href="/">SSAFYRO</a>
                </div>
                {/* 메뉴 자리 */}
                <div className="flex-auto text-center">
                    <a className="mx-3" href="">
                        1차
                    </a>
                    <a className="mx-3" href="">
                        2차
                    </a>
                </div>
                {/* 회원 메뉴 */}
                <div className="flex-auto text-end">
                    <a href="">로그인</a>
                </div>
            </nav>
        </>
    );
}
