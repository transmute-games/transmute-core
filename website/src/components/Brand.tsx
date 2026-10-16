import Link from "next/link";
import Image from "next/image";

export function Brand() {
  return (
    <Link href="/" className="brand" aria-label="Transmute Core home">
      <Image src="/favicon.svg" width={32} height={32} alt="" />
      <span>
        transmute<span className="brand-sub"> / core</span>
      </span>
    </Link>
  );
}
