import type { Metadata } from "next";
import { SkipLink } from "@/components/a11y";
import "./globals.css";

export const metadata: Metadata = {
  title: "Stephen Darcy",
  description:
    "Software engineer focused on polished interfaces, reliable services, and secure delivery practices.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        <SkipLink href="#main-content">Skip to content</SkipLink>
        <main id="main-content" tabIndex={-1}>
          {children}
        </main>
      </body>
    </html>
  );
}
